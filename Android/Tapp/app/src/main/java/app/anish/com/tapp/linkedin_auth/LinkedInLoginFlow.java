package app.anish.com.tapp.linkedin_auth;

/**
 * Interface representing a login flow
 * that is used for retrieving the user's
 * LinkedIn Profile Information
 */

public abstract class LinkedInLoginFlow {
    public abstract void startFlow();

    public interface FlowCompletionListener {
        void onSuccess();
        void onFailure(Throwable throwable);
    }
}
