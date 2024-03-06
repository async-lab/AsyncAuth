import club.asyncraft.asyncauth.server.config.MyModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class test {

    @Test
    public void test() {
        System.out.println(UuidUtil.getTimeBasedUuid().toString().replace("-","").substring(0,16));
    }

    @Test
    public void testToml() throws IOException {
        /*TomlParseResult parse = Toml.parse(Path.of("E:\\Minecraft\\server\\1.18.x\\1.18.2 arclight\\world\\serverconfig", "async_auth-server.toml"));
        System.out.println(parse.getString("database.username"));*/

        CommentedConfig parse = TomlFormat.instance().createParser().parse(new FileReader(new File(Path.of("E:\\Minecraft\\server\\1.18.x\\1.18.2 arclight\\world\\serverconfig\\AsyncAuth\\config.toml").toUri())));
    }

}
