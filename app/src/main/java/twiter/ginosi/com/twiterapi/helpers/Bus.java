package twiter.ginosi.com.twiterapi.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class Bus{
    public HashMap<Class, Object> stickyBus;
    public HashMap<String, List<MessageListener>> eventListeners;

    private static Bus bus = new Bus();

    private Bus(){
        stickyBus = new HashMap<>();
        eventListeners = new HashMap<>();
    }

    public static Bus getInstance() {
        return bus;
    }

    public void pushSticky(Object object) {
        if(object != null) {
            if(object.getClass().getName().endsWith("RealmProxy")){
                stickyBus.put(object.getClass().getSuperclass(), object);
            }else{
                stickyBus.put(object.getClass(), object);
            }
        }
    }

    public <T> T popSticky(Class<T> t) {
        T item = peekSticky(t);
        if(item!= null){
            stickyBus.remove(t);
        }
        return item;
    }

    public <T> T peekSticky(Class<T> t) {
        return (T)stickyBus.get(t);
    }

    public void publish(String event, Object... params) {
        List<MessageListener> listeners = eventListeners.get(event);
        if(listeners != null){
            for(MessageListener listener : listeners){
                listener.onMessage(event, params);
            }
        }
    }

    public void subscribe(String event, MessageListener listener) {
        List<MessageListener> listeners = eventListeners.get(event);
        if(listeners == null){
            listeners = new ArrayList<>();
            eventListeners.put(event, listeners);
        }

        listeners.add(listener);
    }

    public void unsubscribe(String event, MessageListener listener) {
        List<MessageListener> listeners = eventListeners.get(event);
        if(listeners != null){
            listeners.remove(listener);
        }
    }

    public interface MessageListener {
        void onMessage(String event, Object... params);
    }
}