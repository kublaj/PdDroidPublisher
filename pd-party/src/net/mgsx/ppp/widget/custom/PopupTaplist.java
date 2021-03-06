package net.mgsx.ppp.widget.custom;

import java.util.List;

import net.mgsx.ppp.view.PdDroidPatchView;
import net.mgsx.ppp.widget.abs.Taplist;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * {@link Taplist} customization : value is selected by dialog popup instead of touch. 
 */
public class PopupTaplist extends Taplist {

	public final static class Selector extends Dialog {
		List<String> values;
		Integer  selectedValue = null;

		public Selector(Context context, List<String> values) {
			super(context);
			this.values = values;
			
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setTitle("Select value");

			final LinearLayout view = new LinearLayout(getContext());
			final ScrollView sview = new ScrollView(getContext());
			view.setOrientation(LinearLayout.VERTICAL);
			sview.setVerticalScrollBarEnabled(true);


			for (int i = 0; i < values.size(); i++) {
				final Button button = new Button(getContext());
				String s = values.get(i);
				button.setText(s);
				button.setId(i);

				button.setOnClickListener(new View.OnClickListener() {
					@Override	
					public void onClick(View v) {
					
						selectedValue = (Integer) button.getId();
						Selector.this.dismiss();
					}
				});
				view.addView(button);
			}
			sview.addView(view);
			setContentView(sview);
		}
		
		public Integer getSelectedValue(){
			return selectedValue;
		}
	}

	public PopupTaplist(PdDroidPatchView app, String[] atomline) {
		super(app, atomline);
	}

	@Override
	public boolean touchdown(int pid, float x, float y) {

		if (dRect.contains(x, y)) {
			final Selector selector = new Selector(parent.getContext(), getValues());
	
			selector.setOnDismissListener(new OnDismissListener() {				
				@Override
				public void onDismiss(DialogInterface dialog) {
					Integer v = selector.getSelectedValue();
					if (v != null){
						val = v;
						doSend();
						parent.threadSafeInvalidate();
					}
					dialog.dismiss();
				}
			});	
			selector.show();
			return true;
		}
		return false;
	}
}
