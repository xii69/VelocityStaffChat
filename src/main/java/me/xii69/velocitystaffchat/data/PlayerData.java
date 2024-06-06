package me.xii69.velocitystaffchat.data;

import java.util.UUID;

public class PlayerData {

    private final UUID playerId;
    private boolean toggled;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public void toggle() {
        toggled = !toggled;
    }
}
