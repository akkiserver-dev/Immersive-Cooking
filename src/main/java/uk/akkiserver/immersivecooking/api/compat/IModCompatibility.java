package uk.akkiserver.immersivecooking.api.compat;

public interface IModCompatibility {
    String modId();
    boolean checkAvail();
    void init();
}
