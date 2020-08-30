package wtf.boomy.apagoge;

public interface CompletionListener {
    
    /**
     * Called once the Apagoge handler is done with it's tasks.
     *
     * @param handler the handler container
     * @param success true if verified
     */
    public void onFinish(ApagogeHandler handler, boolean success);
}
