package br.com.anso.pdo.selecionarEndereco;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;


public class AutoCompleteEnderecoWidget extends AutoCompleteTextView implements ISelecionarEnderecoView.ISelecionarEnderecoPresenter{

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AutoCompleteEnderecoWidget.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public AutoCompleteEnderecoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        super.onFilterComplete(count);
    }
}
