import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.cloudspace.gcmidfinder.MainActivity;
import com.cloudspace.gcmidfinder.R;

/**
 * Created by FutureHax on 4/14/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity act;
    EditText input;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        act = getActivity();
        input = (EditText) act.findViewById(R.id.input);
    }

    public void testMyFirstTestTextView_labelText() {
        String expected = "HELLO";
        input.setText(expected);
        final String actual = input.getText().toString();
        assertEquals(expected, actual);
    }

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public void testPreconditions() {
        assertNotNull("MainActivity is null", act);
        assertNotNull("input is null", input);
    }
}
