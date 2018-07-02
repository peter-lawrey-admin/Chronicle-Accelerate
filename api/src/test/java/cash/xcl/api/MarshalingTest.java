package cash.xcl.api;

import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.CurrentBalanceResponse;
import cash.xcl.api.dto.SignedBinaryMessage;
import cash.xcl.api.exch.*;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.wire.AbstractMarshallable;
import net.openhft.chronicle.wire.BinaryWire;
import net.openhft.chronicle.wire.TextWire;
import net.openhft.chronicle.wire.Wire;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class MarshalingTest {
    private final Bytes<ByteBuffer> bytes = Bytes.elasticByteBuffer();

    @Parameter(0)
    public String testName;
    @Parameter(1)
    public AbstractMarshallable marshalObject;
    @Parameter(2)
    public Class<? extends AbstractMarshallable> clazz;
    @Parameter(3)
    public Class<?> resultClass;

    @SuppressWarnings("rawtypes")
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() throws IOException {
        String[] files = {"marshaling/currencypair.yaml", "marshaling/cancelordercommand.yaml", "marshaling/executionreport.yaml",
                "marshaling/executionreportevent.yaml", "marshaling/orderclosedevent.yaml", "marshaling/newlimitordercommand.yaml",
                "marshaling/depositvaluecommand.yaml", "marshaling/depositvalueevent.yaml", "marshaling/withdrawvaluecommand.yaml",
                "marshaling/withdrawvalueevent.yaml", "marshaling/currentbalancequery.yaml", "marshaling/currentbalanceresponse.yaml"};
        Class<?>[] objClass = {CurrencyPair.class, CancelOrderCommand.class, ExecutionReport.class, ExecutionReportEvent.class,
                OrderClosedEvent.class, NewOrderCommand.class, DepositValueCommand.class, DepositValueEvent.class,
                WithdrawValueCommand.class, WithdrawValueEvent.class, CurrentBalanceQuery.class, CurrentBalanceResponse.class};
        SignedBinaryMessage.addAliases();
        ArrayList<Object[]> params = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            TextWire textWire = new TextWire(BytesUtil.readFile(files[i]));
            Class<?> crtClass = objClass[i];
            Map<String, Map> allTestData = new HashMap<>();
            textWire.readAllAsMap(String.class, Map.class, allTestData);
            System.out.println("Found " + allTestData.size() + " test scenarios");
            allTestData.forEach((k, v) -> {
                Map<?, ?> testData = v;
                Object input = testData.get("input");
                System.out.println(input);
                AbstractMarshallable obj = (AbstractMarshallable) input;
                Class<?> resultClass = (Class<?>) testData.get("output");
                // testMarshall(resultClass, k, obj, resultClass);
                Object[] allParams = new Object[4];
                allParams[0] = k;
                allParams[1] = obj;
                allParams[2] = crtClass;
                allParams[3] = resultClass;
                params.add(allParams);
            });
        }
        return params;
    }

    @Test
    public void testMarshalling() {
        System.out.println("Testing: " + testName);
        try {
            doMarshalingTest(marshalObject, clazz, createBinaryWire());
            if ((resultClass != null) && Throwable.class.isAssignableFrom(resultClass)) {
                fail("Expected error " + resultClass + " did not triger");
            }
        } catch (Exception ex) {
            if ((resultClass == null) || !resultClass.equals(ex.getClass())) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        // text wire is not validating stuff
        doMarshalingTest(marshalObject, clazz, createTextWire());
        System.out.println("Passed:  " + testName);
    }

    public void doMarshalingTest(AbstractMarshallable outObj, Class<?> clazz, Wire wire) {
        AbstractMarshallable inObj = (AbstractMarshallable) ObjectUtils.newInstance(clazz);
        assertNotEquals(outObj, inObj);
        outObj.writeMarshallable(wire);
        inObj.readMarshallable(wire);
        assertEquals(outObj, inObj);
    }

    private BinaryWire createBinaryWire() {
        bytes.clear();
        return new BinaryWire(bytes);
    }

    private TextWire createTextWire() {
        bytes.clear();
        return new TextWire(bytes);
    }

    @After
    public void cleanup() {
        bytes.release();
    }

}
