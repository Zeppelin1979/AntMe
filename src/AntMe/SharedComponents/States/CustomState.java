package AntMe.SharedComponents.States;

import java.util.HashMap;
import java.util.Set;

public class CustomState {
	private HashMap<String, Object> kvalues = new HashMap<String, Object>();

    /// <summary>
    /// Gets a value indicating whether this instance has value.
    /// </summary>
    /// <value><c>true</c> if this instance has value; otherwise, <c>false</c>.</value>
    public boolean hasValue() {
    	if (kvalues == null) {
    		return false;
    	}
    	if (kvalues.isEmpty()) {
    		return false;
    	}
    	return true;
    }

    /// <summary>
    /// Adds the specified key.
    /// </summary>
    /// <param name="key">The key.</param>
    /// <param name="value">The value.</param>
    public void add(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }
        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        kvalues.put(key, value);
    }

    /// <summary>
    /// Removes the specified key.
    /// </summary>
    /// <param name="key">The key.</param>
    /// <returns></returns>
    public boolean remove(String key) {
        if (key == null) {
            return false;
        }

        if (!kvalues.containsKey(key)) {
            return false;
        }
    	kvalues.remove(key);
        return true;
    }

    /// <summary>
    /// Removes the specified value.
    /// </summary>
    /// <param name="value">The value.</param>
    /// <returns></returns>
    public boolean remove(Object value) {
        if (value == null) {
            return false;
        }
        if (! kvalues.containsValue(value)) {
            return false;
        }
        String key="";
        for (String keyIt: kvalues.keySet()) {
        	if (kvalues.get(keyIt) == value) {
        		key = keyIt;
        		break;
        	}
        }
        kvalues.remove(key);
        return true;
    }

    /// <summary>
    /// Gets or sets the <see cref="System.Object"/> with the specified key.
    /// </summary>
    /// <value></value>
    public Object get(String key) {
    	return kvalues.get(key);
    }
    
    public void set(String key, Object Value) {
    	kvalues.put(key, Value);
    }

    /// <summary>
    /// Gets a list of available keys.
    /// </summary>
    public Set<String> getKeys() {
        return kvalues.keySet();
    }
}

