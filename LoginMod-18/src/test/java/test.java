import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;

public class test {

    @Test
    public void test() {
        System.out.println(UuidUtil.getTimeBasedUuid().toString().replace("-","").substring(0,16));
    }

}
