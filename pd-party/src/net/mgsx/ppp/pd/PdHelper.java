package net.mgsx.ppp.pd;

import java.util.ArrayList;
import java.util.List;

import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.PdDispatcher;

import android.util.Log;

public class PdHelper 
{
	private static PdDispatcher dispatcher;
	private static StringBuilder message = new StringBuilder();
	
	public static void init() 
	{
		dispatcher = new PdDispatcher() {
			@Override
			public void print(String s) {
				if(s.endsWith("\n"))
				{
					message.append(s.substring(0, s.length()-1));
					Log.i("Pd [print]", message.toString());
					message = new StringBuilder();
				}
				else
				{
					message.append(s);
				}
			}
		};
		PdBase.setReceiver(dispatcher);
	}
	
	public static void addListener(String symbol, PdListener listener) 
	{
		dispatcher.addListener(symbol, listener);
	}


	/** send a Pd atom-string 's' to a particular receiver 'dest' */
	public static void send(String dest, String s) 
	{
		List<Object> list = new ArrayList<Object>();
		String[] bits = s.split(" ");
		
		for (int i=0; i < bits.length; i++) {
			try {
				list.add(Float.parseFloat(bits[i]));
			} catch (NumberFormatException e) {
				list.add(bits[i]);
			}
		}
		
		Object[] ol = list.toArray();
		PdBase.sendList(dest, ol);
	}

}
