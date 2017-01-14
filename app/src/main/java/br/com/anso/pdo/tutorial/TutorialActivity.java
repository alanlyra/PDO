package br.com.anso.pdo.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import br.com.anso.pdo.R;
import br.com.anso.pdo.principal.PrincipalActivity;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_1));
        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_2));
        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_3));
        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_4));
        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_5));
        addSlide(TutorialFragments.newInstance(R.layout.tutorial_fragment_6));

        setBarColor(Color.TRANSPARENT);
        setSeparatorColor(Color.parseColor("#FFFFFF"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPager().setCurrentItem(getPager().getCurrentItem()+1);
            }
        });

        setSkipText(getText(R.string.skip));
        setDoneText(getText(R.string.concluido));

        setVibrate(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(TutorialActivity.this, PrincipalActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(TutorialActivity.this, PrincipalActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
    
}

