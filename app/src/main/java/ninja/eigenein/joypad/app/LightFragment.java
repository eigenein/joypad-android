package ninja.eigenein.joypad.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ninja.eigenein.joypad.JoypadView;

public class LightFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_light, container, false);

        final TextView textView = (TextView)view.findViewById(R.id.text_view);

        final JoypadView joypadView = (JoypadView)view.findViewById(R.id.joypad);
        joypadView.setListener(new JoypadView.Listener() {
            @Override
            public void onUp() {
                textView.setText(R.string.text_view_up);
            }

            @Override
            public void onMove(final float distance, final float dx, final float dy) {
                textView.setText(getString(R.string.text_view_move, distance, dx, dy));
            }
        });

        return view;
    }
}
