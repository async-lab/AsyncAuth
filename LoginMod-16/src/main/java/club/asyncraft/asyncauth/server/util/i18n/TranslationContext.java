package club.asyncraft.asyncauth.server.util.i18n;

import club.asyncraft.asyncauth.AsyncAuth;
import club.asyncraft.asyncauth.common.network.ClientMessageDTO;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.JarURLConnection;
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

    public static void init() throws IOException {

        File localsDir = new File(LANG_CONFIG_PATH);
        if (!localsDir.exists()) {
            localsDir.mkdir();
        }

        /*createTempDir(AsyncAuth.class.getClassLoader().getResourceAsStream("locals"),file -> {
            for (File listFile : file.listFiles()) {
                System.out.println(listFile.getName());
            }
        });*/

        String resourcePath = AsyncAuth.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        System.out.println(resourcePath);
        //TODO 获取jar包路径
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

    private static void createTempDir(InputStream inputStream, Consumer<File> execution) throws IOException {
        File tempDir = new File(FMLPaths.GAMEDIR.get().toUri().getPath(),"asyncauthtemp");

        BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(tempDir.toPath()));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int data = bufferedInputStream.read();
        while (data != -1) {
            outputStream.write(data);
            data = bufferedInputStream.read();
        }
        outputStream.flush();
        outputStream.close();
        bufferedInputStream.close();
        execution.accept(tempDir);
        tempDir.deleteOnExit();
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
