package ir.companymeerkats.myapplication.view;
public interface ActionPlaying {
    void nextClicked(int position);
    void prevClicked(int position);
    void playClicked(int position);
    void cancel();
}