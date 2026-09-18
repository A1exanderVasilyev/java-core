import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class MyStringBuilder {
    private enum OperationType {
        APPEND,
        INSERT,
        DELETE
    }

    private record Snapshot(
            OperationType type,
            int pos,
            int length,
            char[] deletedChars
    ) {
        Snapshot {
            deletedChars = deletedChars == null ? null : deletedChars.clone();
        }

        @Override
        public char[] deletedChars() {
            return deletedChars == null ? null : deletedChars.clone();
        }
    }

    private static final int INIT_CAPACITY = 16;
    private static final int MAX_HISTORY_SIZE = 10;

    private char[] storage;
    private int count;
    private final Deque<Snapshot> history;

    public MyStringBuilder() {
        this.storage = new char[INIT_CAPACITY];
        this.count = 0;
        this.history = new ArrayDeque<>();
    }

    private void ensureCapacity(int minimumCapacity) {
        int currLen = storage.length;
        if (minimumCapacity <= currLen) {
            return;
        }

        int newCapacity = currLen * 2;
        while (newCapacity < minimumCapacity) {
            newCapacity *= 2;
        }

        storage = Arrays.copyOf(storage, newCapacity);
    }

    private void addSnapshot(Snapshot shot) {
        if (history.size() == MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.addLast(shot);
    }

    public MyStringBuilder undo() {
        if (history.isEmpty()) {
            return this;
        }

        Snapshot shot = history.removeLast();
        OperationType type = shot.type;
        int pos = shot.pos;
        int len = shot.length;
        switch (type) {
            case APPEND -> {
                count = len;
            }
            case INSERT -> {
                System.arraycopy(storage, pos + len, storage, pos, count - pos - len);
                count -= len;
            }
            case DELETE -> {
                char[] charsToRestore = shot.deletedChars;
                int restoreLen = charsToRestore.length;
                ensureCapacity(count + restoreLen);
                System.arraycopy(storage, pos, storage, pos + restoreLen, count - pos);
                System.arraycopy(charsToRestore, 0, storage, pos, restoreLen);
                count += restoreLen;
            }
        }

        return this;
    }

    public MyStringBuilder append(String str) {
        if (str == null) {
            throw new NullPointerException("str is null");
        }

        int len = str.length();
        addSnapshot(new Snapshot(OperationType.APPEND, count, count, null));

        ensureCapacity(count + len);
        str.getChars(0, len, storage, count);
        count += len;
        return this;
    }

    public MyStringBuilder insert(int index, String str) {
        if (str == null) {
            throw new NullPointerException("str is null");
        }
        if (index < 0 || index > count) {
            throw new IndexOutOfBoundsException("index should be in range from 0 to " + count);
        }

        int insertLen = str.length();
        addSnapshot(new Snapshot(OperationType.INSERT, index, insertLen, null));
        ensureCapacity(count + insertLen);
        System.arraycopy(storage, index, storage, index + insertLen, count - index);
        str.getChars(0, insertLen, storage, index);
        count += insertLen;

        return this;
    }

    public MyStringBuilder delete(int start, int end) {
        if (start == end) {
            return this;
        }
        if (end > count) {
            end = count;
        }
        if (start < 0 || start > end) {
            throw new IndexOutOfBoundsException("wrong delete range");
        }
        int len = end - start;
        if (len > 0) {
            addSnapshot(new Snapshot(OperationType.DELETE, start, end - start, Arrays.copyOfRange(storage, start, end)));
            System.arraycopy(storage, end, storage, start, count - end);
            count -= len;
        }
        return this;
    }

    public int length() {
        return count;
    }

    @Override
    public String toString() {
        return new String(storage, 0, count);
    }
}
