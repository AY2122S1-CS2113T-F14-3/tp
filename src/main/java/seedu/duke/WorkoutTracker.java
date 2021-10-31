package seedu.duke;

import seedu.duke.exceptions.schedule.ScheduleException;
import seedu.duke.exceptions.workout.DeleteWorkoutException;
import seedu.duke.exceptions.workout.DuplicateWorkoutException;
import seedu.duke.exceptions.workout.MissingWorkoutCalorieSeparatorException;
import seedu.duke.exceptions.workout.MissingWorkoutDescriptionException;
import seedu.duke.exceptions.workout.NoWorkoutIndexException;
import seedu.duke.exceptions.workout.WorkoutException;
import seedu.duke.exceptions.workout.WorkoutNullArgumentException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//@@author arvejw

/**
 * Manages the tracking of workouts and various other operations such as adding new workouts and the calories burned
 * doing them as well as deleting said workouts.
 */
public class WorkoutTracker {
    protected ArrayList<String> workouts;
    protected String workoutDescription;
    protected int caloriesBurned;
    protected String workoutDate;
    protected String workoutTime;
    private static final String INPUT_ALL = "all";
    private static final int LOWER_BOUND_INDEX_NON_EMPTY_LIST_ONES_INDEXING = 1;
    public static final Logger WORKOUT_TRACKER_LOGGER = Logger.getLogger("WorkoutTrackerLogger");

    public WorkoutTracker() {
        this.workouts = new ArrayList<>();
        WORKOUT_TRACKER_LOGGER.setLevel(Level.SEVERE);
    }

    /**
     * Generates the workout parameters based off the user's input.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @throws WorkoutException
     */
    public void generateWorkoutParameters(String inputArguments) throws WorkoutException {
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Starting generation of parameters for workout.");
        workoutDescription = Parser.getDescription(inputArguments);
        caloriesBurned = Parser.getCaloriesBurnedForWorkout(inputArguments);
        workoutDate = Parser.getDate(inputArguments);
        workoutTime = Parser.getTime(inputArguments);
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Successfully generated parameters for workout.");
    }

    /**
     * Adds a workout to the workout list.
     *
     * @param inputArguments      Arguments input by the user that come after the command word.
     * @param isSquelchAddMessage Flag to determine whether to squelch the message printed during successful adding of
     *                            a workout. <code>true</code> to squelch, <code>false</code> to continue printing
     *                            the message.
     * @throws WorkoutException Issue during adding the workout.
     */
    public void addWorkout(String inputArguments, boolean isSquelchAddMessage) throws WorkoutException {
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Starting to try and add workout.");
        nullArgumentCheck(inputArguments);
        assert inputArguments != null : "Exception should already been thrown if argument is null";
        missingDescriptionCheck(inputArguments);
        generateWorkoutParameters(inputArguments);
        String updatedArguments = workoutDescription + Parser.CALORIE_SEPARATOR + caloriesBurned
                + Parser.DATE_SEPARATOR + workoutDate + Parser.TIME_SEPARATOR + workoutTime;
        duplicateWorkoutCheck(updatedArguments);
        if (!isSquelchAddMessage) {
            System.out.println("Noted! CLI.ckFit has recorded your workout of description \"" + workoutDescription
                    + "\" on " + workoutDate + " at " + workoutTime + " where you burned "
                    + caloriesBurned + " calories!");
        }
        workouts.add(updatedArguments);
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Successfully added workout.");
    }

    /**
     * Deletes a workout from the workout list.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @throws WorkoutException Issue during deletion of the workout.
     */
    public void deleteWorkout(String inputArguments) throws WorkoutException {
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Starting to try and delete workout.");
        if (inputArguments == null) {
            throw new NoWorkoutIndexException();
        }
        assert inputArguments != null : "Exception should already been thrown if argument is null";
        if (isWorkoutListEmpty()) {
            System.out.println(ClickfitMessages.EMPTY_WORKOUT_LIST_MESSAGE);
            return;
        }
        assert workouts.size() > 0 : "List should be non empty at this point";
        int workoutNumber = Parser.parseStringToInteger(inputArguments);
        int workoutIndex = workoutNumber - 1; // 0-indexing
        if (isWorkoutNumberWithinRange(workoutNumber)) {
            generateWorkoutParameters(workouts.get(workoutIndex));
            System.out.println("Noted! CLI.ckFit has successfully deleted your recorded workout of description \""
                    + workoutDescription + "\" on " + workoutDate + " at " + workoutTime + System.lineSeparator()
                    + " where you burned " + caloriesBurned + " calories!");
            workouts.remove(workoutIndex);
            WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Successfully deleted workout.");
        } else {
            WORKOUT_TRACKER_LOGGER.log(Level.WARNING, "Failed to delete workout.");
            throw new DeleteWorkoutException();
        }
    }

    /**
     * Prints out the list of recorded workouts.
     * Either a filtered list based off a certain date or the full workout list can be printed.
     *
     * @param inputArguments Date to use as a filter in the format dd/mm/yyyy.
     *                       If the input is <code>all</code> the full list of all workouts recorded is printed.
     */
    public void listWorkouts(String inputArguments) throws WorkoutException {
        if (inputArguments.equals(INPUT_ALL)) {
            listAllWorkouts();
        } else {
            listWorkoutsOnDate(inputArguments);
        }
    }

    /**
     * Prints out the full list of all recorded workouts.
     *
     * @throws WorkoutException Issue during generation of parameters when printing all workouts.
     */
    public void listAllWorkouts() throws WorkoutException {
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Starting to try and list workouts.");
        if (isWorkoutListEmpty()) {
            System.out.println(ClickfitMessages.EMPTY_WORKOUT_LIST_MESSAGE);
            return;
        }
        assert workouts.size() > 0 : "List should be non empty at this point";
        System.out.println("All recorded workouts:" + System.lineSeparator() + ClickfitMessages.ENDLINE_PRINT_FORMAT);
        int currentIndex = 1;
        for (String workout : workouts) {
            generateWorkoutParameters(workout);
            System.out.println(currentIndex + ". " + workoutDescription);
            System.out.println("Calories burned: " + caloriesBurned);
            System.out.println("Date: " + workoutDate);
            System.out.println("Time: " + workoutTime + System.lineSeparator());
            currentIndex++;
        }
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Successfully listed workouts.");
    }

    /**
     * Prints out a filtered workout list based off a certain date.
     *
     * @param inputArguments Date to use as a filter in the format dd/mm/yyyy.
     *                       If the input is <code>all</code> the full list of all workouts recorded is printed.
     * @throws WorkoutException Issue during generation of parameters when printing all workouts.
     */
    public void listWorkoutsOnDate(String inputArguments) throws WorkoutException {
        ArrayList<String> filteredWorkoutList = (ArrayList<String>) workouts.stream()
                .filter((t) -> Parser.getDate(t).equals(inputArguments)).collect(Collectors.toList());
        if (filteredWorkoutList.isEmpty()) {
            if (inputArguments.equals(Parser.getSystemDate())) {
                System.out.println(ClickfitMessages.EMPTY_WORKOUT_LIST_ON_DATE_MESSAGE);
            } else {
                System.out.println("No workouts recorded on the date: " + inputArguments);
            }
        } else {
            if (inputArguments.equals(Parser.getSystemDate())) {
                System.out.println("Workouts recorded today:"
                        + System.lineSeparator() + ClickfitMessages.ENDLINE_PRINT_FORMAT);
            } else {
                System.out.println("Workouts recorded on " + inputArguments + ":"
                        + System.lineSeparator() + ClickfitMessages.ENDLINE_PRINT_FORMAT);
            }
            int currentIndex = 1;
            int totalCaloriesBurned = 0;
            for (String workout : filteredWorkoutList) {
                generateWorkoutParameters(workout);
                System.out.println(currentIndex + ". " + workoutDescription);
                System.out.println("Calories burned: " + caloriesBurned);
                System.out.println("Date: " + workoutDate);
                System.out.println("Time: " + workoutTime + System.lineSeparator());
                totalCaloriesBurned += caloriesBurned;
                currentIndex++;
            }
            System.out.println("Total calories burned: " + totalCaloriesBurned + System.lineSeparator()
                    + ClickfitMessages.ENDLINE_PRINT_FORMAT);
        }
    }

    /**
     * Returns the calories burned on a particular date based on the workouts recorded in the workout list.
     *
     * @param date Date to use as a filter in the format dd/mm/yyyy when getting total calories.
     * @return The total calories burned on the date. If no workouts recorded on the date, total calories burned is 0.
     * @throws WorkoutException Issue getting calories burned from workouts.
     */
    public int getCaloriesBurned(String date) throws WorkoutException {
        ArrayList<String> filteredWorkoutList = (ArrayList<String>) workouts.stream()
                .filter((t) -> Parser.getDateNoDateTracker(t).equals(date)).collect(Collectors.toList());
        int totalCaloriesBurned = 0;
        if (!filteredWorkoutList.isEmpty()) {
            for (String workout : workouts) {
                totalCaloriesBurned += Parser.getCaloriesBurnedForWorkout(workout);
            }
        }
        return totalCaloriesBurned;
    }

    /**
     * Checks whether input argument is null.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @throws WorkoutException Argument is null.
     */
    public void nullArgumentCheck(String inputArguments) throws WorkoutException {
        if (inputArguments == null) {
            WORKOUT_TRACKER_LOGGER.log(Level.WARNING, "User input argument(s) is null.");
            throw new WorkoutNullArgumentException();
        }
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "User input argument(s) is not null.");
    }

    /**
     * Checks whether the list of recorded workouts is empty.
     *
     * @return <code>true</code> if workout list is empty, <code>false</code> otherwise.
     */
    public boolean isWorkoutListEmpty() {
        return workouts.isEmpty();
    }

    /**
     * Checks whether the workout index is within range.
     * This check is done under the assumption that ones-indexing is used.
     *
     * @param workoutNumber Index of the workout.
     * @return <code>true</code> if within range, <code>false</code> otherwise.
     */
    public boolean isWorkoutNumberWithinRange(int workoutNumber) {
        int upperBound = workouts.size();
        int lowerBound = LOWER_BOUND_INDEX_NON_EMPTY_LIST_ONES_INDEXING;
        return (workoutNumber >= lowerBound) && (workoutNumber <= upperBound);
    }

    /**
     * Checks whether the description of the workout is missing in the user input.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @throws WorkoutException Unable to find description or find separators.
     */
    public void missingDescriptionCheck(String inputArguments) throws WorkoutException {
        int indexOfFirstCalorieSeparator = inputArguments.indexOf(Parser.CALORIE_SEPARATOR.trim());
        String subStringBeforeCalorieSeparator = "";
        if (indexOfFirstCalorieSeparator != -1) { // calorie separator not found
            subStringBeforeCalorieSeparator = inputArguments.substring(0, indexOfFirstCalorieSeparator).trim();
        } else {
            throw new MissingWorkoutCalorieSeparatorException();
        }
        if (subStringBeforeCalorieSeparator.isEmpty()) {
            WORKOUT_TRACKER_LOGGER.log(Level.WARNING, "Description is missing in user input arguments.");
            throw new MissingWorkoutDescriptionException();
        }
        WORKOUT_TRACKER_LOGGER.log(Level.INFO, "Description is present in user input arguments.");
    }

    /**
     * Checks whether a duplicate workout already exists in the list.
     *
     * @param inputArguments The arguments of the workout to be potentially added.
     * @throws WorkoutException Duplicate workout detected.
     */
    public void duplicateWorkoutCheck(String inputArguments) throws WorkoutException {
        for (String w : workouts) {
            if (inputArguments.trim().equals(w)) {
                throw new DuplicateWorkoutException();
            }
        }
    }
}
