package dev.crifurch.unlimitedadmin;

import com.mojang.logging.LogUtils;
import dev.crifurch.unlimitedadmin.modules.chat.ChatConfig;
import dev.crifurch.unlimitedadmin.modules.chat.ChatModule;
import dev.crifurch.unlimitedadmin.modules.stop.commands.CommandStop;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(UnlimitedAdmin.MODID)
public class UnlimitedAdmin {

    public static final String MODID = "unlimited_admin";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UnlimitedAdmin() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ChatModule());


        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
        MinecraftForge.EVENT_BUS.addListener(this::fixTabNamesMixin);
        MinecraftForge.EVENT_BUS.addListener(this::hackAdmin);
        MinecraftForge.EVENT_BUS.addListener(this::onChangeDimension);

    }

    private void setup(final FMLCommonSetupEvent event) {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChatConfig.serverSpec);
        eventBus.register(ChatConfig.class);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        CommandStop.register(event.getDispatcher());
    }

    //todo remove this hack
    public void hackAdmin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().getGameProfile().getName().equals("FenixOfDeath")) {
            final Collection<MutableComponent> prefixes = event.getPlayer().getPrefixes();
            prefixes.clear();
            prefixes.add(new TextComponent("§c[Admin§c]§r"));
        }
        final ServerPlayer player = (ServerPlayer) event.getPlayer();
        player.refreshTabListName();

    }

    public void fixTabNamesMixin(PlayerEvent.TabListNameFormat event) {
        event.setDisplayName(new TextComponent(event.getPlayer().getDisplayName().getString() + "(" + event.getPlayer().getLevel().dimension().location().getPath() + ")"));
    }

    public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.getPlayer().getLevel().isClientSide) {
            return;
        }
        final ServerPlayer player = (ServerPlayer) event.getPlayer();
        player.refreshTabListName();
 }
}
