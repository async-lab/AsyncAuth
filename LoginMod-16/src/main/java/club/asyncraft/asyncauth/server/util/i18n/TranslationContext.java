package club.asyncraft.asyncauth.server.util.i18n;

import club.asyncraft.asyncauth.AsyncAuth;
import club.asyncraft.asyncauth.common.network.ClientMessageDTO;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class TranslationContext {

    private static CommentedConfig langConfig;
    private static String prefix;

    private static final String LANG_CONFIG_PATH;

    public static ClientMessageDTO clientMessage;

    static {
        String path = ServerLifecycleHooks.getCurrentServer().getServerDirectory().getAbsolutePath();
        Path localsPath = Paths.get(path.substring(0, path.length()-2),"world","serverconfig","AsyncAuth", "locals");
        LANG_CONFIG_PATH = localsPath.toString();
    }

    public static void init() throws IOException, URISyntaxException {

        File localsDir = new File(LANG_CONFIG_PATH);
        if (!localsDir.exists()) {
            localsDir.mkdir();
        }

        InputStream resourceStream = AsyncAuth.class.getClassLoader().getResourceAsStream("");
        JarInputStream jarInputStream = new JarInputStream(resourceStream);
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            String name = jarEntry.getName();
            if (name.startsWith("locals") && !jarEntry.isDirectory()) {
                languageConfigHandle(TranslationContext.class.getClassLoader().getResourceAsStream(name),name);
            }
        }

        File langFile = new File(LANG_CONFIG_PATH, MyModConfig.language.get() + ".toml");
        if (!langFile.exists()) {
            throw new FileNotFoundException("language file not found");
        }
        langConfig = TomlFormat.instance().createParser().parse(Files.newInputStream(langFile.toPath()),StandardCharsets.UTF_8);

        prefix = ((String) langConfig.get("prefix")).replace("&","§") + "§r";

        clientMessage = new ClientMessageDTO();
        clientMessage.setAlreadyLoginMsg(translate("login.already_login"));
        clientMessage.setPasswordTooShort(translate("login.too_short"));
        clientMessage.setMinLength(MyModConfig.minLength.get());
        clientMessage.setLoginCommandUsage(translate("command.login_usage"));
        clientMessage.setRegisterCommandUsage(translate("command.register_usage"));
        clientMessage.setPasswordUnconformity(translate("command.password_unconformity"));
//        TomlParseResult parseResult = Toml.parse(langFile.getAbsolutePath());
        //langConfig = new ConfigMap(parseResult);
    }

    private static void languageConfigHandle(InputStream inputStream,String name) throws IOException {
        String fileName = name.substring(7);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8));
        File file = new File(LANG_CONFIG_PATH,fileName);
        if (file.exists()) {
            return;
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        int charData = reader.read();
        while (charData != -1) {
            writer.write(charData);
            charData = reader.read();
        }
        reader.close();
        writer.flush();
        writer.close();
    }

    public static String translate(String path) {
        return convertMessage(langConfig.get(path));
    }

    public static String convertMessage(String msg) {
        return prefix + msg.replace("&","§");
    }

}
