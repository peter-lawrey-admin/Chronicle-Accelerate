package town.lost.examples.appreciation.api;


import im.xcl.platform.dto.ApplicationError;

public interface AppreciationResultsListener {
    void onBalance(OnBalance onBalance);

    void applicationError(ApplicationError applicationError);
}
