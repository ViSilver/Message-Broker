package warehouse;

import common.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vi
 */
public class DataAccessObject<T extends Identifiable> {

    public class Item<T> {
        public Item(T data, String origin) {
            this.data = data;
            this.origin = origin;
            this.timestamp = new Date();
        }

        public T getData() {
            return data;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getOrigin() {
            return origin;
        }

        public void setData(T newData) {
            this.data = newData;
            this.timestamp = new Date();
        }

        private T data;
        private Date timestamp;
        private String origin;
    }

    public DataAccessObject() {
        items = Collections.synchronizedList(new ArrayList<Item<T>>());
    }

    public int add(T data, String origin) {
        synchronized (items) {
            int id = items.size();
            data.setID(id);
            items.add(new Item(data, origin));
            return id;
        }
    }

    public void update(int i, T data) throws IndexOutOfBoundsException {
        synchronized (items) {
            data.setID(i);
            items.get(i).setData(data);
        }
    }

    public T get(int i) throws IndexOutOfBoundsException {
        synchronized (items) {
            return items.get(i).getData();
        }
    }

    public List<T> getList() {
        synchronized (items) {
            return items.stream().map(Item::getData).collect(Collectors.toList());
        }
    }

    public List<T> getList(int offset, int limit) {
        synchronized (items) {
            if (offset > items.size() || limit < 0) {
                return new ArrayList<T>();
            }

            if (offset < 0) {
                offset = 0;
            }

            if (limit == 0) {
                limit = items.size();
            }

            int toIndex = offset + limit;

            if (toIndex > items.size()) {
                toIndex = items.size();
            }

            return items.subList(offset, toIndex).stream().map(Item::getData).collect(Collectors.toList());
        }
    }

    public List<T> getChangesSince(Date timestamp) {
        synchronized (items) {
            return items.stream()
                    .filter(itm -> itm.timestamp.after(timestamp))
                    .map(Item::getData)
                    .collect(Collectors.toList());
        }
    }

    public List<T> getChangesSince(Date timestamp, String origin) {
        synchronized (items) {
            return items.stream()
                    .filter(itm -> itm.timestamp.after(timestamp) && itm.origin.equals(origin))
                    .map(Item::getData)
                    .collect(Collectors.toList());
        }
    }

    private List<Item<T>> items;
}
