package itacademy.snowadv.fightinggamep2p.Classes;

import androidx.fragment.app.Fragment;

public interface NotifiableActivity {
    void notify(Fragment fragment);
    void notifyFragmentIsDone(Fragment fragment);
    void notifyWithObject(Object object);
}
