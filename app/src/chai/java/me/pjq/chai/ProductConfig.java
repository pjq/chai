package me.pjq.chai;

/**
 * Configuration related to the product flavor Chai
 */
public enum ProductConfig implements ProductConfigInterface{
    INSTANCE;

    @Override
    public String getProductFlavorName() {
        return "Chai";
    }
}
