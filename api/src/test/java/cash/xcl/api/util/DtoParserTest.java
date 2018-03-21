package cash.xcl.api.util;

import cash.xcl.api.AllMessages;
import cash.xcl.api.DtoParser;
import cash.xcl.api.dto.BaseDtoParser;
import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Mocker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class DtoParserTest {
    @Parameter(0)
    public String name;

    @Parameter(1)
    public int methodId;

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IllegalAccessException {
        List<Object[]> tests = new ArrayList<>();
        for (Field field : MessageTypes.class.getFields()) {
            if (field.getType() != int.class) {
                continue;
            }
            Object[] test = {field.getName(), field.getInt(null)};
            tests.add(test);
        }
        return tests;
    }

    @Test
    public void allMethodIds() {
        Bytes<?> bytes = Bytes.allocateDirect(512);
        DtoParser parser = new BaseDtoParser();
        bytes.zeroOut(0, bytes.realCapacity());
        bytes.readLimit(DtoParser.MESSAGE_OFFSET + 2);
        bytes.writeUnsignedShort(DtoParser.PROTOCOL_OFFSET, 1);
        bytes.writeUnsignedShort(DtoParser.MESSAGE_OFFSET, methodId);
        // make sure it can be parsed and has the expected messageType
        parser.parseOne(bytes, Mocker.intercepting(AllMessages.class,
                (String method, Object[] args) -> {
                    SignedMessage arg0 = (SignedMessage) args[0];
                    //                    System.out.println(arg0.getClass().getName()+".class,");
                    assertEquals(name, methodId, arg0.messageType());
                },
                null));
    }
}
