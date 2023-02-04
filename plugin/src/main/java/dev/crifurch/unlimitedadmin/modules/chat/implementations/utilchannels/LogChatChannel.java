package dev.crifurch.unlimitedadmin.modules.chat.implementations.utilchannels;

import dev.crifurch.unlimitedadmin.api.providers.PluginFileProvider;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.chat.data.sender.ChatMessageSender;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.IChatChanel;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ILoggedChat;
import dev.crifurch.unlimitedadmin.modules.chat.interfaces.ISubhandlerChannel;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LogChatChannel implements ISubhandlerChannel {
    private static final Pattern pattern = Pattern.compile("\u00A7.");
    final ChatModule chatModule;
    private File logFile;

    public LogChatChannel(ChatModule chatModule) {
        this.chatModule = chatModule;
    }

    @Override
    public @NotNull ChatModule getModule() {
        return chatModule;
    }

    @Override
    public @NotNull String getName() {
        return "log";
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onSubhandlerMessage(@NotNull ChatMessageSender sender, IChatChanel parent, String message) {
        final IChatChanel iChatChanel = ChatModule.unwrapChannel(parent);
        if (!(iChatChanel instanceof ILoggedChat)) {
            return;
        }
        String logPrefix = ((ILoggedChat) iChatChanel).getLogPrefix();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[dd/MM/yyyy HH:mm:ss]");
        DateTimeFormatter dtFile = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        LocalDateTime now = LocalDateTime.now();
        String logFileName = dtFile.format(now) + ".log";
        logFile = PluginFileProvider.UnlimitedAdmin.getModuleFile(chatModule.getDefinition(), "logs", logFileName);
        logFile.setWritable(true);
        String formatMessage = pattern.matcher(message).replaceAll("");
        String logMessage = dtf.format(now) + " " + logPrefix + " " + formatMessage;
        try {
            final OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(logFile.toPath(), StandardOpenOption.APPEND), StandardCharsets.UTF_8);
            BufferedWriter output = new BufferedWriter(out);

            output.append(logMessage).append(System.lineSeparator());
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
