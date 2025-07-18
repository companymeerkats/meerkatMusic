package ir.companymeerkats.myapplication.view.service;
public interface ActionPlaying {
    void nextClicked(int position);
    void prevClicked(int position);
    void playClicked(int position);
    void cancel();
}