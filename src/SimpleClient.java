import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.events.ITangoChangeListener;
import fr.esrf.TangoApi.events.TangoChange;
import fr.esrf.TangoApi.events.TangoChangeEvent;
import fr.esrf.TangoApi.events.TangoEventsAdapter;

import javax.swing.*;
import java.util.Date;
import java.lang.*;

public class SimpleClient {
	
	private DeviceProxy host = null;
	private String[] attributes = {"State"};
	private static String[] filters = new String[0];
	private TangoEventsAdapter supplier = null;
	private StateEventListener state_listener = null;
	
	public SimpleClient(String name) {
		try {
			host = new DeviceProxy(name);	
		} catch (DevFailed e) {
			System.err.println("Error creating DeviceProxy");
		}
	}
	
	public void subscribeChangeStateEvent() {
        String stringError = null;
        try {

            if (supplier == null)
                supplier = new TangoEventsAdapter(host);


            state_listener = new StateEventListener();
            supplier.addTangoChangeListener(
                                state_listener, "State", filters);
        } catch (DevFailed e) {
            state_listener = null;

            //System.err.println(host.name());
            //	Display exception
            if (!e.errors[0].desc.startsWith("Already connected to event"))
                stringError = "subscribeChangeStateEvent() for " +
                        host.get_name() + " FAILED !\n" + e.errors[0].desc;
            //fr.esrf.TangoDs.Except.print_exception(e);
			//System.err.println(host.get_name() + ":	"+e.errors[0].desc);
        } catch (Exception e) {
            state_listener = null;
            //	Display exception
            stringError = "subscribeChangeStateEvent() for " +
                    host.get_name() + " FAILED !" + e.toString();
            e.printStackTrace();
        }
    }

	class StateEventListener implements ITangoChangeListener {
	    //=====================================================================
	    //=====================================================================
	    public void change(TangoChangeEvent event) {
	
	        //long	t0 = System.currentTimeMillis();
	        TangoChange tc = (TangoChange) event.getSource();
	        String deviceName = tc.getEventSupplier().get_name();
	        DevState hostState;
	        DevState notifdState;
	
	        System.out.println("Received Event");
	        int timeout = -1;
	        try {
	            //	Get the host state from attribute value
	            DeviceAttribute attr = event.getValue();
	            if (attr.hasFailed())
	                hostState = DevState.UNKNOWN;
	            else
	                hostState = attr.extractState();
	
	        } catch (DevFailed e) {
	            System.err.println(new Date());
	            System.err.println(host.name() + "  has received a DevFailed :	" + e.errors[0].desc);
	            hostState = DevState.ALARM;
	            System.err.println("HostStateThread.StateEventListener on " + deviceName);
	            try {
	                timeout = host.get_timeout_millis();
	                host.set_timeout_millis(500);
	                host.ping();
	            } catch (DevFailed e2) {
	                hostState = DevState.FAULT;
	            }
	        } catch (Exception e) {
	            System.out.println("AstorEvent." + deviceName);
	            System.out.println(e);
	            System.out.println("HostStateThread.StateEventListener : could not extract data!");
	            hostState = DevState.UNKNOWN;
	        }
	        try {
	            if (timeout>0)
	                host.set_timeout_millis(timeout);
	        }catch (DevFailed e) {
	            System.err.println(e.errors[0].desc);
	        }
	
	    }
    }
	
	public static void main(String [] args)
	{
		if (args.length != 1) {
			System.err.println("Wrong usage");
	        System.exit(1);
		}
		SimpleClient client = new SimpleClient(args[0]);
		client.subscribeChangeStateEvent();
		try {
			   // thread to sleep for 1000 milliseconds
		    Thread.sleep(1000);
	    } catch (Exception e) {
		    System.out.println(e);
	    }
	}
}
