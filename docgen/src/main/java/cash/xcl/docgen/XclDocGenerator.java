package cash.xcl.docgen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import com.sun.jna.platform.win32.WinDef.SHORT;

import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.wire.TextWire;

/**
 * = Documentation generator for the XCL Protocol
 * <p>
 * This is an annotation processor which generates documentation for the XCl protocol.
 * The following annotation are processed:
 * <p>
 * - link:XclCommand.html[XclCommand]
 * - link:XclParam.html[XclParam]
 */
@SupportedAnnotationTypes({"cash.xcl.docgen.XclCommand", "cash.xcl.docgen.XclEvent", "cash.xcl.docgen.XclMessageField"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class XclDocGenerator extends AbstractProcessor {

    private final String titlePattern;
    private final String outputPattern;
    private final String structureBegin;
    private final String tableLine;

    public XclDocGenerator() {
        try {
            titlePattern = loadTextResource("title.txt");
            outputPattern = loadTextResource("output-events.txt");
            structureBegin = loadTextResource("message-structure-table.txt");
            tableLine = loadTextResource("message-structure-table-row.txt");
            Files.createDirectories(Paths.get("target", "xcldocs"));
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    private static String loadTextResource(String resourceName) throws IOException {
        URL titleUrl = XclDocGenerator.class.getResource(resourceName);
        TextWire textWire = new TextWire(BytesUtil.readFile(titleUrl.getPath()));
        return textWire.toString();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            return false;
        }
        Elements elements = processingEnv.getElementUtils();
        Messager messager = processingEnv.getMessager();

        processXclCommand(roundEnvironment, elements, messager);
        processXclEvent(roundEnvironment, elements, messager);
        return false;
    }


    private void processXclCommand(RoundEnvironment roundEnvironment, Elements elements, Messager messager) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(XclCommand.class)) {
            TypeElement typeElement = (TypeElement) element;
            XclCommand xclAnnot = typeElement.getAnnotation(XclCommand.class);

            try (PrintWriter pw = new PrintWriter(
                    "target/xcldocs/" + generateName(element.getSimpleName().toString(), "-").toLowerCase() + ".adoc");) {
                String title = generateName(element.getSimpleName().toString());
                pw.println(MessageFormat.format(titlePattern, title, xclAnnot.id()));

                String javadoc = elements.getDocComment(typeElement);
                if (javadoc != null) {
                    pw.println(javadoc);
                }
                pw.println(MessageFormat.format(outputPattern, generateName(xclAnnot.success()), getMessageId(xclAnnot.success()),
                        generateName(xclAnnot.failure()), getMessageId(xclAnnot.failure())));

                printMessageStructure(element, pw);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void processXclEvent(RoundEnvironment roundEnvironment, Elements elements, Messager messager) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(XclEvent.class)) {
            TypeElement typeElement = (TypeElement) element;
            XclEvent xclAnnot = typeElement.getAnnotation(XclEvent.class);
            try (PrintWriter pw = new PrintWriter(
                    "target/xcldocs/" + generateName(element.getSimpleName().toString(), "-").toLowerCase() + ".adoc");) {

                String title = generateName(element.getSimpleName().toString());
                pw.println(MessageFormat.format(titlePattern, title, xclAnnot.id()));
                String javadoc = elements.getDocComment(typeElement);
                if (javadoc != null) {
                    pw.println(javadoc);
                }
                printMessageStructure(element, pw);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }

        }

    }

    private void printMessageStructure(Element element, PrintWriter pw) {
        pw.println(structureBegin);
        int crtValue = 82;
        boolean fixedSize = true;
        for (Element subElement : element.getEnclosedElements()) {
            if (subElement instanceof VariableElement) {
                VariableElement varElement = (VariableElement) subElement;
                XclMessageField fielddAnnot = varElement.getAnnotation(XclMessageField.class);
                if (fielddAnnot != null) {
                    TypeMirror varType = varElement.asType();
                    TypeKind typeKind = varType.getKind();
                    TypeElement elem = processingEnv.getElementUtils().getTypeElement(varType.toString());
                    String typeName = elem != null ? elem.getSimpleName().toString() : varType.toString();
                    if (fixedSize) {
                        int size = -1;
                        if (typeKind.isPrimitive()) {
                            size = sizeForPrimitiveType(typeKind);
                        } else {
                            assert elem != null;
                            TypeKind pTypeKind = wrapperToPrimitveMap.get(elem.getQualifiedName().toString());
                            if (pTypeKind != null) {
                                size = sizeForPrimitiveType(pTypeKind);
                                typeName = pTypeKind.name().toLowerCase();
                            } else {
                                if (elem.getKind() == ElementKind.ENUM) {
                                    size = Integer.BYTES;
                                } else {
                                    fixedSize = false;
                                }
                            }
                        }
                        if (fixedSize) {
                            pw.println(MessageFormat.format(tableLine, crtValue, crtValue += size, fielddAnnot.name(), typeName));
                        } else {
                            pw.println(MessageFormat.format(tableLine, "Var", "Length", fielddAnnot.name(), typeName));
                        }
                    } else {
                        pw.println(MessageFormat.format(tableLine, "Var", "Length", fielddAnnot.name(), typeName));
                    }
                }
            }
        }
        pw.println("|===");
    }

    private String getMessageId(String className) {
        if (className.equals("None")) {
            return "";
        }
        try {
            XclEvent annot = Class.forName(className).getAnnotation(XclEvent.class);
            if (annot != null) {
                return annot.id();
            }
        } catch (Exception ex) {
        }
        return "Unknown";

    }

    private static String generateName(String nameNoSpaces) {
        return generateName(nameNoSpaces, " ");
    }

    private static String generateName(String nameNoSpaces, String join) {
        if (nameNoSpaces.contains(".")) {
            nameNoSpaces = nameNoSpaces.substring(nameNoSpaces.lastIndexOf(".") + 1);
        }
        StringJoiner joiner = new StringJoiner(join);
        Arrays.stream(nameNoSpaces.split("(?=\\p{Upper})")).forEach((s) -> joiner.add(s));
        return joiner.toString();
    }


    private static final Map<String, TypeKind> wrapperToPrimitveMap = new HashMap<>();

    static {
        wrapperToPrimitveMap.put(Boolean.class.getName(), TypeKind.BOOLEAN);
        wrapperToPrimitveMap.put(Byte.class.getName(), TypeKind.BYTE);
        wrapperToPrimitveMap.put(Character.class.getName(), TypeKind.CHAR);
        wrapperToPrimitveMap.put(Short.class.getName(), TypeKind.SHORT);
        wrapperToPrimitveMap.put(Integer.class.getName(), TypeKind.INT);
        wrapperToPrimitveMap.put(Long.class.getName(), TypeKind.LONG);
        wrapperToPrimitveMap.put(Double.class.getName(), TypeKind.DOUBLE);
        wrapperToPrimitveMap.put(Float.class.getName(), TypeKind.FLOAT);
    }

    private static int sizeForPrimitiveType(TypeKind typeKind) {
        switch (typeKind) {
            case BOOLEAN:
                return Byte.SIZE;
            case BYTE:
                return Byte.SIZE;
            case CHAR:
                return Character.SIZE;
            case DOUBLE:
                return Double.SIZE;
            case FLOAT:
                return Float.SIZE;
            case INT:
                return Integer.SIZE;
            case LONG:
                return Long.SIZE;
            case SHORT:
                return SHORT.SIZE;
            default:
                return -1;

        }

    }
}
