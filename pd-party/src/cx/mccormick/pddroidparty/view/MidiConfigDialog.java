package cx.mccormick.pddroidparty.view;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import cx.mccormick.pddroidparty.R;
import cx.mccormick.pddroidparty.midi.MidiDevice;
import cx.mccormick.pddroidparty.midi.MidiInput;
import cx.mccormick.pddroidparty.midi.MidiManager;
import cx.mccormick.pddroidparty.midi.MidiOutput;
import cx.mccormick.pddroidparty.midi.ip.MidiInputCreateDialog;
import cx.mccormick.pddroidparty.midi.ip.MidiOutputCreateDialog;
import cx.mccormick.pddroidparty.midi.nmj.NMJMidiDevice;

public class MidiConfigDialog extends Dialog
{
	private MidiManager midiManager;
	
	private Spinner midiInSpinner;

	private LinearLayout lv;
	
	private ArrayAdapter<MidiPortAdapter> midiInArrayList;

	public MidiConfigDialog(Context context, MidiManager midiManager) 
	{
		super(context);
		this.midiManager = midiManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setTitle("MIDI Configuration");
		
		LinearLayout view = new LinearLayout(getContext());
		view.setOrientation(LinearLayout.VERTICAL);
		Button button = new Button(getContext());
		button.setText("Ok");
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MidiConfigDialog.this.dismiss();
			}
		});
		
		final TextView inLabel = new TextView(getContext());
		inLabel.setText("MIDI input");

		midiInArrayList = new ArrayAdapter<MidiPortAdapter>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<MidiPortAdapter>());
		midiInSpinner = new Spinner(getContext(), Spinner.MODE_DIALOG);
		midiInSpinner.setAdapter(midiInArrayList);
		midiInArrayList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		midiInSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				MidiPortAdapter element = midiInArrayList.getItem(position);
				midiManager.setIn((MidiInput)element.midiPort);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
			}
		});
		
		
		refreshInputList();
		
		Button btCreateIn = new Button(getContext());
		btCreateIn.setText("+");
		btCreateIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MidiInputCreateDialog dialog = new MidiInputCreateDialog(getContext(), midiManager.getIPMidiDevice());
				dialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						refreshInputList();
					}
				});
				dialog.show();
			}
		});
		
		final TextView outLabel = new TextView(getContext());
		outLabel.setText("MIDI outputs");

		ScrollView sv = new ScrollView(getContext());
		sv.setFillViewport(true);
		sv.setVerticalScrollBarEnabled(true);
		lv = new LinearLayout(getContext());
		lv.setOrientation(LinearLayout.VERTICAL);

		refreshOutputList();
		
		sv.addView(lv);
		
		Button btCreateOut = new Button(getContext());
		btCreateOut.setText("+");
		btCreateOut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MidiOutputCreateDialog dialog = new MidiOutputCreateDialog(getContext(), midiManager.getIPMidiDevice());
				dialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						refreshOutputList();
					}
				});
				dialog.show();
			}
		});

		LinearLayout inLayout = new LinearLayout(getContext());
		
		inLayout.addView(inLabel);
		inLayout.addView(btCreateIn);
		inLayout.addView(midiInSpinner);
		view.addView(inLayout);
		
		LinearLayout outLayout = new LinearLayout(getContext());
		outLayout.addView(outLabel);
		outLayout.addView(btCreateOut);
		outLayout.addView(sv);
		view.addView(outLayout);
		
		LinearLayout setup = new LinearLayout(getContext());
		setup.setOrientation(LinearLayout.HORIZONTAL);
		
		// TODO use device abstraction
		for(MidiDevice device : midiManager.getDeveices())
		{
			if(device instanceof NMJMidiDevice)
			{
				final NMJMidiDevice nmjDevice = (NMJMidiDevice)device;
				ImageButton btSetup = new ImageButton(getContext());
				btSetup.setImageResource(R.drawable.ic_action_network_wifi);
				btSetup.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						nmjDevice.openConfigPanel();
					}
				});
				setup.addView(btSetup);
			}
		}
		
		// TODO use USBMidiDevice instead ...
		ImageButton btUsb = new ImageButton(getContext());
		btUsb.setImageResource(R.drawable.ic_action_usb);
		btUsb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				midiManager.usbMidiManager.chooseMidiDevice();
			}
		});

		setup.addView(btUsb);
		
		setup.addView(button);
		
		view.addView(setup);
		
		setContentView(view);
	}
	
	protected void refreshOutputList()
	{
		lv.removeAllViews();
		
		for(MidiDevice midiDevice : midiManager.getDeveices())
		{
			for(MidiOutput midiOutput : midiDevice.getOutputs())
			{
				lv.addView(createOutputWidget(midiDevice, midiOutput));
			}
		}
		
	}
	
	private View createOutputWidget(MidiDevice midiDevice, final MidiOutput midiOutput) 
	{
		LinearLayout view = new LinearLayout(getContext());
		
		TextView tv = new TextView(getContext());
		tv.setText(new MidiPortAdapter(midiDevice, midiOutput).toString());
		
		CheckBox cb = new CheckBox(getContext());
		cb.setChecked(midiManager.isOpened(midiOutput));
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					midiManager.open(midiOutput);
				}
				else
				{
					midiManager.close(midiOutput);
				}
			}
		});
		
		view.addView(cb);
		view.addView(tv);
		
		return view;
	}

	protected void refreshInputList() 
	{
		OnItemSelectedListener listener = midiInSpinner.getOnItemSelectedListener();
		midiInSpinner.setOnItemSelectedListener(null);
		midiInArrayList.clear();
		midiInArrayList.add(new MidiPortAdapter());
		int position = 0;
		for(MidiDevice midiDevice : midiManager.getDeveices())
		{
			for(MidiInput midiInput : midiDevice.getInputs())
			{
				if(midiInput == midiManager.getInput())
				{
					position = midiInArrayList.getCount();
				}
				midiInArrayList.add(new MidiPortAdapter(midiDevice, midiInput));
			}
		}
		midiInSpinner.setSelection(position);
		midiInSpinner.setOnItemSelectedListener(listener);
	}
}
