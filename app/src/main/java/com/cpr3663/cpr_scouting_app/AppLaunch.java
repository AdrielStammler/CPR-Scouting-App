package com.cpr3663.cpr_scouting_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cpr3663.cpr_scouting_app.databinding.AppLaunchBinding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AppLaunch extends AppCompatActivity {
    // =============================================================================================
    // Define constants
    // =============================================================================================
    private static final long SPLASH_SCREEN_DELAY = 2_000;
    private static final String NO_TEAM = "No Team Exists";
    private static final String NO_MATCH = "No Match";
    private static final String NO_COMPETITION = "No Competition Name Exists";
    private static final int NO_EVENT = 999;

    // =============================================================================================
    // Class:       MatchInfoRow
    // Description: Defines a structure/class to hold the information for each Match
    // Methods:     getListOfTeams()
    //                  return an array of team numbers (6 of them) that are in this match
    // =============================================================================================
    public static class MatchInfoRow {
        // Class Members
        protected int red1 = 0;
        protected int red2 = 0;
        protected int red3 = 0;
        protected int blue1 = 0;
        protected int blue2 = 0;
        protected int blue3 = 0;

        public MatchInfoRow(String csvRow)
        {
            if (!csvRow.equals(NO_MATCH)) {
                String[] data = csvRow.split(",");
                red1 = Integer.valueOf(data[2]);
                red2 = Integer.valueOf(data[3]);
                red3 = Integer.valueOf(data[4]);
                blue1 = Integer.valueOf(data[5]);
                blue2 = Integer.valueOf(data[6]);
                blue3 = Integer.valueOf(data[7]);
            }
        }

        public MatchInfoRow(String in_red1, String in_red2, String in_red3, String in_blue1, String in_blue2, String in_blue3)
        {
            red1 = Integer.valueOf(in_red1);
            red2 = Integer.valueOf(in_red2);
            red3 = Integer.valueOf(in_red3);
            blue1 = Integer.valueOf(in_blue1);
            blue2 = Integer.valueOf(in_blue2);
            blue3 = Integer.valueOf(in_blue3);
        }

        public int[] getListOfTeams() {
            int[] list = { red1, red2, red3, blue1, blue2, blue3};
            return list;
        }
    }

    // =============================================================================================
    // Class:       DeviceInfoRow
    // Description: Defines a structure/class to hold the information for each Device.
    // Methods:     getDeviceNumber()
    //                  returns the (int) device number for this row.
    //              getTeamNumber()
    //                  returns the (int) team number for this row.
    //              getDescription()
    //                  returns the (String) description for this row.
    // =============================================================================================
    public static class DeviceInfoRow {
        // Class Members
        protected int device_number = 0;
        protected int team_number = 0;
        protected String description = "";

        public DeviceInfoRow(String in_device_number, String in_team_number, String in_description)
        {
            device_number = Integer.valueOf(in_device_number);
            team_number = Integer.valueOf(in_team_number);
            description = in_description;
        }

        public int getDeviceNumber() {
            return device_number;
        }

        public int getTeamNumber() {
            return team_number;
        }

        public String getDescription() {
            return description;
        }
    }

    // =============================================================================================
    // Class:       DNPInfoRow
    // Description: Defines a structure/class to hold the information for each DNP reason
    // Methods:     getId()
    //                  returns the (int) DNP number for this row to use for logging
    //              getDescription()
    //                  returns the (String) description for this row
    // =============================================================================================
    public static class DNPInfoRow {
        // Class Members
        protected int id = 0;
        protected String description = "";

        public DNPInfoRow(String in_id, String in_description)
        {
            id = Integer.valueOf(in_id);
            description = in_description;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }
    }

    // =============================================================================================
    // Class:       EventInfo
    // Description: Defines a structure/class to hold the information for all Event Info
    // Methods:     addEventRow()
    //                  adds a row to the list of events
    //              getEventsForPhase()
    //                  returns an array of possible initial events in the given match phase
    //              getNextEvents(int EventId)
    //                  returns an array of possible Events that can happen after EventId
    //              getEventId(String EventDescription)
    //                  returns the internal ID for an event
    //                  useful for logging events that don't happen on the field of play.
    // =============================================================================================
    public static class EventInfo {
        // Class Members
        protected ArrayList<EventInfoRow> event_list;

        // Class constants
        public static final String EVENT_STARTING_NOTE = "";
        public static final String EVENT_DEFENDED_START = "";
        public static final String EVENT_DEFENDED_END = "";
        public static final String EVENT_DEFENSE_START = "";
        public static final String EVENT_DEFENSE_END = "";
        public static final String EVENT_PHASE_NONE = Match.PHASE_NONE;
        public static final String EVENT_PHASE_AUTO = Match.PHASE_AUTO;
        public static final String EVENT_PHASE_TELEOP = Match.PHASE_TELEOP;

        public EventInfo()
        {
            event_list = new ArrayList<EventInfoRow>();
        }

        public void addEventRow(String in_id, String in_description, String in_phase, String in_FOP, String in_seq_start, String in_seq_end) {
            event_list.add(new EventInfoRow(Integer.valueOf(in_id), in_description, in_phase, Boolean.valueOf(in_FOP), Boolean.valueOf(in_seq_start), Boolean.valueOf(in_seq_end)));
        }

        public String[][] getEventsForPhase(String in_phase) {

            // Error check the input and only do this if they passed in a valid parameter
            if (in_phase.equals(EVENT_PHASE_NONE)) return new String[][] {{""}};

            int arr_length = 0;
            for (EventInfoRow eventInfoRow : event_list) {
                if (eventInfoRow.match_phase.equals(in_phase) && eventInfoRow.is_FOP_Event && eventInfoRow.is_seq_start) arr_length++;
            }
            String[][] ret = new String[arr_length][2];
            int index = 0;

            for (EventInfoRow eventInfoRow : event_list) {
                // Only build the array if the phase is right AND this is for a FOP (field of play) AND this event starts a sequence
                if (eventInfoRow.match_phase.equals(in_phase) && eventInfoRow.is_FOP_Event && eventInfoRow.is_seq_start) {
                    ret[index][0] = String.valueOf(eventInfoRow.id);
                    ret[index][1] = eventInfoRow.description;
                    index++;
                }
            }
            return ret;
        }

        public String[][] getNextEvents(int in_EventId) {
            String in_phase = "";

            // Find the event in the list, and get it's PHASE
            for (EventInfoRow eventInfoRow : event_list) {
                if (eventInfoRow.id == in_EventId) {
                    in_phase = eventInfoRow.match_phase;
                    break;
                }
            }

            // Get the needed length of the array
            int arr_length = 0;
            for (EventInfoRow eventInfoRow : event_list) {
                // Figure out if its valid and add one if it is
                if (eventInfoRow.match_phase.equals(in_phase) && eventInfoRow.is_FOP_Event && eventInfoRow.is_seq_end) arr_length++;
            }
            if (arr_length == 0) return new String[][] {{""}};

            String[][] ret = new String[arr_length][2];
            int index = 0;

            // Now find all events that match the phase AND are for a FOP (field of play) AND ends a sequence
            for (EventInfoRow eventInfoRow : event_list) {
                // Only build the array if the phase is right AND this is for a FOP (field of play) AND this event starts a sequence
                if (eventInfoRow.match_phase.equals(in_phase) && eventInfoRow.is_FOP_Event && eventInfoRow.is_seq_end) {
                    ret[index][0] = String.valueOf(eventInfoRow.id);
                    ret[index][1] = eventInfoRow.description;
                    index++;
                }
            }
            return ret;
        }

        public int getEventId(String in_EventDescription) {
            int ret = NO_EVENT;

            // Look through the event rows to find a match
            for (int i = 0; i < event_list.size(); i++) {
                if (event_list.get(i).description.equals(in_EventDescription)) {
                    ret = event_list.get(i).id;
                    break;
                }
            }

            return ret;
        }

        // =============================================================================================
        // Class:       EventInfoRow (PRIVATE)
        // Description: Defines a structure/class to hold the information for each Event
        // Methods:     n/a
        // =============================================================================================
        private class EventInfoRow {
            // Class Members
            protected int id = 0;
            protected String match_phase = "";
            protected String description = "";
            protected boolean is_FOP_Event = false;
            protected boolean is_seq_start = false;
            protected boolean is_seq_end = false;

            public EventInfoRow(int in_id, String in_description, String in_phase, Boolean in_FOP, Boolean in_seq_start, Boolean in_seq_end)
            {
                id = in_id;
                description = in_description;
                match_phase = in_phase;
                is_FOP_Event = in_FOP;
                is_seq_start = in_seq_start;
                is_seq_end = in_seq_end;
            }
        }
    }

    // =============================================================================================
    // Global variables
    // =============================================================================================
    private AppLaunchBinding applaunchbinding;
    public static ArrayList<String> TeamList = new ArrayList<String>();
    public static ArrayList<String> CompetitionList = new ArrayList<String>();
    public static ArrayList<MatchInfoRow> MatchList = new ArrayList<MatchInfoRow>();
    public static ArrayList<DeviceInfoRow> DeviceList = new ArrayList<DeviceInfoRow>();
    public static ArrayList<DNPInfoRow> DNPList = new ArrayList<DNPInfoRow>();
    public static EventInfo EventList = new EventInfo();
    public static int CompetitionId = 4;

    @SuppressLint({"DiscouragedApi", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        applaunchbinding = AppLaunchBinding.inflate(getLayoutInflater());
        View page_root_view = applaunchbinding.getRoot();
        setContentView(page_root_view);
        ViewCompat.setOnApplyWindowInsetsListener(applaunchbinding.appLaunch, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        page_root_view.setBackgroundColor(R.color.cpr_bkgnd);
        LoadTeamData();
        LoadCompetitionData();
        LoadDeviceData();
        LoadDNPData();
        LoadMatchData();
        LoadEventData();

        // Small delay to show the splash screen
        try {
            Thread.sleep(SPLASH_SCREEN_DELAY);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Go to the first page
        Intent GoToNextPage = new Intent(AppLaunch.this, PreMatch.class);
        startActivity(GoToNextPage);
    }

    // =============================================================================================
    // Function:    LoadTeamData
    // Description: Read the list of teams from the .csv file into the global TeamList structure
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadTeamData(){
        String line = "";
        int index = 1;

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_teams));

        // Open the asset file holding all of the Teams information (~10,000 records)
        // Read each line and add the team name into the ArrayList and use the team number as
        // the index to the array (faster lookups).  We need to then ensure that if there's a gap
        // in team numbers, we fill the Array with a "NO_TEAM" entry so subsequent teams are
        // matched with their corresponding index into the ArrayList
        try {
            TeamList.add(NO_TEAM);

            InputStream is = getAssets().open(getResources().getString(R.string.file_teams));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                // Need to make sure there's no gaps so the team number and index align
                for (int i = index; i < Integer.valueOf(info[0]); i++) {
                    TeamList.add(NO_TEAM);
                }
                TeamList.add(info[1]);
                index = Integer.valueOf(info[0]) + 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================================================
    // Function:    LoadCompetitionData
    // Description: Read the list of competitions from the .csv file into the global
    //              CompetitionList structure.  This is used for ADMIN configuration of the device.
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadCompetitionData(){
        String line = "";
        int index = 1;

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_competitions));

        // Open the asset file holding all of the Competition information
        // Read each line and add the compeition name into the ArrayList and use the competition id
        // as the index to the array.  We need to then ensure that if there's a gap in competition
        // numbers, we fill the Array with a "NO_COMPETITON" entry so subsequent competitions are
        // matched with their corresponding index into the ArrayList.  This should never happen.
        try {
            CompetitionList.add(NO_COMPETITION);

            InputStream is = getAssets().open(getResources().getString(R.string.file_competitions));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                // Need to make sure there's no gaps so the team number and index align
                for (int i = index; i < Integer.valueOf(info[0]); i++) {
                    CompetitionList.add(NO_COMPETITION);
                }
                CompetitionList.add(info[1]);
                index = Integer.valueOf(info[0]) + 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================================================
    // Function:    LoadMatchData
    // Description: Read the list of matches from the .csv file into the global
    //              MatchList structure.
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadMatchData(){
        String line = "";
        int index = 1;

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_matches));

        // Open the asset file holding all of the Match information
        // Read each line and add the match information into the MatchList and use the match number
        // as the index to the array.
        //
        // This list also uses an array of MatchRowInfo since we're storing more than 1 value.
        try {
            MatchList.add(new MatchInfoRow(NO_MATCH));

            InputStream is = getAssets().open(getResources().getString(R.string.file_matches));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                // Use only the match information that equals the competition we're in.
                if (Integer.valueOf(info[0]) == CompetitionId) {
                    for (int i = index; i < Integer.valueOf(info[1]); i++) {
                        MatchList.add(new MatchInfoRow(NO_MATCH));
                    }
                    MatchList.add(new MatchInfoRow(info[2], info[3], info[4], info[5], info[6], info[7]));
                    index = Integer.valueOf(info[1]) + 1;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================================================
    // Function:    LoadDeviceData
    // Description: Read the list of devices from the .csv file into the global
    //              DeviceList structure.
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadDeviceData(){
        String line = "";

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_devices));

        // Open the asset file holding all of the Device information
        // Read each line and add the device information into the DeviceList.  There is no mapping
        // of the device number and the index into the array (there's no need)
        //
        // This list also uses an array of DeviceRowInfo since we're storing more than 1 value.
        try {
            InputStream is = getAssets().open(getResources().getString(R.string.file_devices));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                DeviceList.add(new DeviceInfoRow(info[0], info[1], info[2]));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================================================
    // Function:    LoadDNPData
    // Description: Read the list of devices from the .csv file into the global
    //              DeviceList structure.
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadDNPData(){
        String line = "";

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_dnp));

        // Open the asset file holding all of the Device information
        // Read each line and add the device information into the DeviceList.  There is no mapping
        // of the device number and the index into the array (there's no need)
        //
        // This list also uses an array of DeviceRowInfo since we're storing more than 1 value
        try {
            InputStream is = getAssets().open(getResources().getString(R.string.file_dnp));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                // Only load "active" DNP reasons
                if (info[2].equals("ACTIVE")) {
                    DNPList.add(new DNPInfoRow(info[0], info[1]));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================================================
    // Function:    LoadEventData
    // Description: Read the list of devices from the .csv file into the global
    //              EventList structure.
    // Output:      void
    // Parameters:  n/a
    // =============================================================================================
    public void LoadEventData(){
        String line = "";

        applaunchbinding.statustext.setText(getResources().getString(R.string.loading_events_auto));

        // Open the asset file holding all of the Event information for AUTO
        // Read each line and add the event information into the EventList.  There is no mapping
        // of the event number and the index into the array (there's no need)
        //
        // This list also uses an array of EventRowInfo since we're storing more than 1 value
        try {
            InputStream is = getAssets().open(getResources().getString(R.string.file_events_auto));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                EventList.addEventRow(info[0], info[1], EventInfo.EVENT_PHASE_AUTO, info[2], info[3], info[4]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
