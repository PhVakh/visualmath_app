package ru.visualmath.android.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import ru.visualmath.android.R;

public class SignUpActivity extends MvpAppCompatActivity implements SignUpView {

    @InjectPresenter
    SignUpPresenter presenter;

    @BindView(R.id.email)
    EditText emailEditText;

    @BindView(R.id.first_name)
    EditText firstNameEditText;

    @BindView(R.id.last_name)
    EditText lastNameEditText;

    @BindView(R.id.middle_name)
    EditText middleNameEditText;

    @BindView(R.id.password)
    EditText passwordEditText;

    @BindView(R.id.passwordConfirm)
    EditText passwordConfirmEditText;

    @BindView(R.id.university)
    EditText universityEditText;

    @BindView(R.id.university_group)
    EditText universityGroupEditText;

    @BindView(R.id.signUp)
    Button signUpButton;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Observable.combineLatest(
                RxTextView.textChanges(emailEditText),
                RxTextView.textChanges(lastNameEditText),
                RxTextView.textChanges(firstNameEditText),
                RxTextView.textChanges(middleNameEditText),
                RxTextView.textChanges(passwordEditText),
                RxTextView.textChanges(passwordConfirmEditText),
                RxTextView.textChanges(universityEditText),
                RxTextView.textChanges(universityGroupEditText),
                (email, last_name, first_name, middle_name, password, passwordConfirm, institution, group) -> email.length() > 0 &&
                        last_name.length() > 0 && first_name.length() > 0 && middle_name.length() > 0 && password.length() > 0 &&
                        passwordConfirm.length() > 0 && institution.length() > 0 && group.length() > 0
        ).subscribe(signUpButton::setEnabled);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String last_name = lastNameEditText.getText().toString();
            String first_name = firstNameEditText.getText().toString();
            String middle_name = middleNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String passwordConfirm = passwordConfirmEditText.getText().toString();
            String university = universityEditText.getText().toString();
            String university_group = universityGroupEditText.getText().toString();
            presenter.onSignUp(email, first_name, last_name, middle_name, password, passwordConfirm, university, university_group);
        });
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.setOnCancelListener(null);
            dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void signIn(String username, String password) {
        Intent data = new Intent();
        data.putExtra("username", username);
        data.putExtra("password", password);
        setResult(0, data);
        finish();
    }

    @Override
    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.sign_in_error)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, id) -> presenter.onErrorCancel())
                .setOnCancelListener(d -> presenter.onErrorCancel());
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showError(@StringRes int messageId) {
        showError(getString(messageId));
    }

    @Override
    public void hideError() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
