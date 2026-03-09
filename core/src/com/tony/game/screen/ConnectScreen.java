package com.tony.game.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kw.gdx.BaseGame;
import com.kw.gdx.asset.Asset;
import com.kw.gdx.screen.BaseScreen;
import kw.tony.net.client.NetworkEventSubscriber;
import kw.tony.net.client.NetworkService;
import kw.tony.net.client.NetworkServiceProvider;
import kw.tony.net.client.event.AvailableServersChangedEvent;
import kw.tony.net.client.event.ConnectionFailedEvent;
import kw.tony.net.client.event.DiscoveredServer;
import kw.tony.net.client.event.InitialWorldStateEvent;

import java.util.List;

public class ConnectScreen extends BaseScreen implements NetworkEventSubscriber {
    private NetworkService networkService;
    private BitmapFont font;
    private Label statusLabel;
    private Table serverListTable;
    private TextButton.TextButtonStyle buttonStyle;

    public ConnectScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void initView() {
        super.initView();
        networkService = resolveNetworkService();
        font = Asset.getAsset().loadBitFont("Manrope-ExtraBold_40_1.fnt");
        font.getData().setScale(1.4f);

        Texture whiteTexture = Asset.getAsset().getTexture("white.png");
        TextureRegionDrawable buttonBaseDrawable = new TextureRegionDrawable(new TextureRegion(whiteTexture));

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle statusStyle = new Label.LabelStyle(font, Color.LIGHT_GRAY);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonBaseDrawable.tint(new Color(0.16f, 0.21f, 0.28f, 1f));
        buttonStyle.down = buttonBaseDrawable.tint(new Color(0.22f, 0.29f, 0.37f, 1f));
        buttonStyle.over = buttonBaseDrawable.tint(new Color(0.18f, 0.25f, 0.33f, 1f));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;

        Table layout = new Table();
        layout.setFillParent(true);
        layout.pad(80f, 60f, 60f, 60f);
        layout.defaults().growX().padBottom(16f);
        addActor(layout);

        Label titleLabel = new Label("Select Server", titleStyle);
        statusLabel = new Label("Refresh to discover available servers.", statusStyle);

        TextButton refreshButton = new TextButton("Refresh", buttonStyle);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                setStatus("Searching for reachable servers...");
                networkService.refreshAvailableServers();
            }
        });

        serverListTable = new Table();
        serverListTable.top();
        ScrollPane scrollPane = new ScrollPane(serverListTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        layout.add(titleLabel).left().row();
        layout.add(statusLabel).left().row();
        layout.add(refreshButton).width(280f).left().row();
        layout.add(scrollPane).grow().row();
    }

    @Override
    public void show() {
        super.show();
        networkService = resolveNetworkService();
        networkService.subscribe(this);
        setStatus("Refresh to discover available servers.");
        networkService.refreshAvailableServers();
    }

    @Override
    public void hide() {
        if (networkService != null) {
            networkService.unsubscribe(this);
        }
        super.hide();
    }

    @Override
    public void onAvailableServersChanged(AvailableServersChangedEvent availableServersChangedEvent) {
        rebuildServerList(availableServersChangedEvent.getServers());
        if (availableServersChangedEvent.getServers().isEmpty()) {
            setStatus("No reachable servers found.");
        } else {
            setStatus("Select a server to join.");
        }
    }

    @Override
    public void onConnecting(DiscoveredServer discoveredServer) {
        setStatus("Connecting to " + discoveredServer.getDisplayAddress() + " ...");
    }

    @Override
    public void onConnected() {
        setStatus("Connected. Waiting for world data...");
    }

    @Override
    public void onDisconnected() {
        setStatus("Disconnected.");
    }

    @Override
    public void onConnectionFailed(ConnectionFailedEvent connectionFailedEvent) {
        setStatus("Connection failed: " + connectionFailedEvent.getMessage());
    }

    @Override
    public void onInitialWorldState(InitialWorldStateEvent initialWorldStateEvent) {
        setScreen(LoadScreen.class);
    }

    @Override
    public void dispose() {
        if (networkService != null) {
            networkService.unsubscribe(this);
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        super.dispose();
    }

    private void rebuildServerList(List<DiscoveredServer> servers) {
        serverListTable.clearChildren();
        serverListTable.defaults().growX().padBottom(12f);

        if (servers.isEmpty()) {
            serverListTable.add(new Label("No servers found on the local network.", new Label.LabelStyle(font, Color.GRAY))).left().row();
            return;
        }

        for (DiscoveredServer server : servers) {
            TextButton serverButton = new TextButton(server.getName() + "  " + server.getDisplayAddress(), buttonStyle);
            serverButton.getLabel().setWrap(true);
            serverButton.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    networkService.connect(server);
                }
            });
            serverListTable.add(serverButton).left().row();
        }
    }

    private void setStatus(String statusText) {
        if (statusLabel != null) {
            statusLabel.setText(statusText);
        }
    }

    private NetworkService resolveNetworkService() {
        if (game instanceof NetworkServiceProvider) {
            return ((NetworkServiceProvider) game).getNetworkService();
        }
        throw new IllegalStateException("Game does not provide a NetworkService");
    }
}
