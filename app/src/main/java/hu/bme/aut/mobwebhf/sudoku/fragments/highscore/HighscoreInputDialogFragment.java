package hu.bme.aut.mobwebhf.sudoku.fragments.highscore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hu.bme.aut.mobwebhf.sudoku.MainActivity;
import hu.bme.aut.mobwebhf.sudoku.R;
import hu.bme.aut.mobwebhf.sudoku.data.database.AppDatabase;
import hu.bme.aut.mobwebhf.sudoku.data.entity.Highscore;
import hu.bme.aut.mobwebhf.sudoku.model.Difficulty;

public class HighscoreInputDialogFragment extends DialogFragment {
    private EditText etName;

    public static final String TAG = "HighscoreDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(getContentView())
                .create();
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_highscore, null);
        etName = contentView.findViewById(R.id.etName);
        Button btnCancel = contentView.findViewById(R.id.btnCancel);
        Button btnOk = contentView.findViewById(R.id.btnOk);
        TextView tvDifficulty = contentView.findViewById(R.id.tvDifficulty);
        TextView tvTime = contentView.findViewById(R.id.tvTime);

        Bundle args = getArguments();

        Difficulty difficulty = Difficulty.valueOf(args.getString("difficulty"));
        String diff = difficulty == Difficulty.EASY ? getString(R.string.easy) : difficulty == Difficulty.MEDIUM ? getString(R.string.medium) : getString(R.string.hard);

        tvDifficulty.setText(getString(R.string.difficulty, diff));
        tvTime.setText(getString(R.string.time, args.getInt("time") / 60, args.getInt("time") % 60));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOk();
            }
        });

        return contentView;
    }

    private void onOk() {
        if (etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.must_fill));
            return;
        }

        Highscore highscore = new Highscore();
        highscore.player = etName.getText().toString();
        highscore.difficulty = getArguments().getString("difficulty");
        highscore.seconds = getArguments().getInt("time");

        insertHighscore(highscore);
        dismiss();
    }

    @SuppressLint("StaticFieldLeak")
    private void insertHighscore(Highscore highscore) {
        new AsyncTask<Highscore, Void, Void>() {
            @Override
            protected Void doInBackground(Highscore... highscores) {
                AppDatabase database = AppDatabase.getInstance(getActivity().getApplicationContext());
                database.highscoreDao().insert(highscores[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getContext(), getString(R.string.save_successful), Toast.LENGTH_SHORT).show();
                MainActivity activity = (MainActivity) getActivity();
                activity.navigateHomeScreen();
            }
        }.execute(highscore);
    }

    private void onCancel() {
        dismiss();

        MainActivity activity = (MainActivity) getActivity();
        activity.navigateHomeScreen();
    }
}
