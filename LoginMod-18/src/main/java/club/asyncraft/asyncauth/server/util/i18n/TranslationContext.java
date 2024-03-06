package club.asyncraft.asyncauth.server.util.i18n;

import club.asyncraft.asyncauth.common.network.ClientMessageDTO;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TranslationContext {

    private static CommentedConfig langConfig;
    private static String prefix;

    private static final String LANG_CONFIG_PATH;

    public static ClientMessageDTO clientMessage;

    static {
        String path = ServerLifecycleHooks.getCurrentServer().getServerDirectory().getAbsolutePath();
        Path localsPath = Path.of(path.substring(0, path.length()-2),"world","serverconfig","AsyncAuth", "locals");
        LANG_CONFIG_PATH = localsPath.toString();
    }

    public static void init() throws IOException {

        File localsDir = new File(LANG_CONFIG_PATH);
        if (!localsDir.exists()) {
            localsDir.mkdir();
        }

        String resourcePath = URLDecoder.decode(TranslationContext.class.getResource("/locals").getPath(),StandardCharsets.UTF_8);

        String jarPath = resourcePath.substring(1, resourcePath.indexOf("#")) + "!/";
        JarFile jarFile = ((JarURLConnection) new URL("jar:file:///" + jarPath).openConnection()).getJarFile();
        Enumeration<JarEntry> jarEntry = jarFile.entries();
        while (jarEntry.hasMoreElements()) {
            JarEntry entry = jarEntry.nextElement();
            String name = entry.getName();
            if (name.startsWith("locals") && !entry.isDirectory()) {
                languageConfigHandle(TranslationContext.class.getClassLoader().getResourceAsStream(name),name);
            }
        }

        File langFile = new File(LANG_CONFIG_PATH, MyModConfig.language.get() + ".toml");
        if (!langFile.exists()) {
            throw new FileNotFoundException("language file not found");
        }
        langConfig = TomlFormat.instance().createParser().parse(new FileInputStream(langFile),StandardCharsets.UTF_8);

        prefix = ((String) langConfig.get("prefix")).replace("&","§") + "§r";

        clientMessage = new ClientMessageDTO();
        clientMessage.setAlreadyLoginMsg(translate("login.already_login"));
        clientMessage.setPasswordTooShort(translate("login.too_short"));
        clientMessage.setLoginCommandUsage(translate("command.login_usage"));
        clientMessage.setRegisterCommandUsage(translate("command.register_usage"));
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,StandardCharsets.UTF_8));
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
