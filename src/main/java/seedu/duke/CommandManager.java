package seedu.duke;

import seedu.duke.exceptions.DukeException;
import seedu.duke.exceptions.MealException;
import seedu.duke.exceptions.FluidExceptions;
import seedu.duke.gym.ScheduleTracker;
import seedu.duke.gym.WorkoutTracker;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

import static seedu.duke.ClickfitMessages.CREDITS;

public class CommandManager {
    protected ScheduleTracker scheduleTracker;
    protected WorkoutTracker workoutTracker;
    protected Meal meal;
    protected Fluid fluid;
    protected WeightTracker weightTracker;
    protected UserHelp userHelp;
    protected Scanner scanner;
    protected boolean isExit;
    protected String command;
    protected String inputArguments;

    public CommandManager(Fluid fluid, Meal meal, ScheduleTracker scheduleTracker, WorkoutTracker workoutTracker,
                          WeightTracker weightTracker, UserHelp userHelp) {
        this.fluid = fluid;
        this.meal = meal;
        this.scheduleTracker = scheduleTracker;
        this.workoutTracker = workoutTracker;
        this.scanner = new Scanner(System.in);
        this.weightTracker = weightTracker;
        this.userHelp = userHelp;
        this.isExit = false;
    }

    public void commandChecker() throws DukeException, NullPointerException, MealException {
        String input = scanner.nextLine();
        String[] splitResults = input.trim().split(" ", 2);
        command = splitResults[0];
        inputArguments = (splitResults.length == 2) ? splitResults[1] : null;
        //assert !input.equals("");
        //assert !Objects.equals(inputArguments, "");
        switch (command) {
        case Keywords.INPUT_MEAL:
            if (splitResults.length == 1) {
                throw new MealException();
            }
            meal.addMeal(inputArguments);
            break;
        case Keywords.DELETE_MEAL:
            meal.deleteMeal(inputArguments);
            break;
        case Keywords.LIST_MEAL:
            meal.listMeals();
            break;
        case Keywords.INPUT_ADD_WORKOUT:
            workoutTracker.addWorkout(inputArguments);
            break;
        case Keywords.INPUT_DELETE_WORKOUT:
            workoutTracker.deleteWorkout(inputArguments);
            break;
        case Keywords.INPUT_LIST_WORKOUT:
            workoutTracker.listWorkouts();
            break;
        case Keywords.INPUT_ADD_SCHEDULE:
            scheduleTracker.addScheduledWorkout(inputArguments);
            break;
        case Keywords.INPUT_DELETE_SCHEDULE:
            scheduleTracker.deleteScheduledWorkout(inputArguments);
            break;
        case Keywords.INPUT_LIST_SCHEDULE:
            scheduleTracker.listScheduledWorkouts();
            break;
        case Keywords.INPUT_DRINKS:
            if (inputArguments != null) {
                try {
                    fluid.addFluid(inputArguments);
                } catch (DateTimeParseException | FluidExceptions e) {
                    System.out.println("Please enter in the format: [fluid_name] /c [calorie_intake] "
                            + "/v [volume] /d [dd/mm/yyyy] /t [hh:mm]");
                }
            } else {
                System.out.println("Please enter in the format: [fluid_name] /c [calorie_intake] "
                        + "/v [volume] /d [dd/mm/yyyy] /t [hh:mm]");
            }
            break;
        case Keywords.DELETE_DRINKS:
            if (inputArguments != null) {
                if (fluid.fluidArray.size() == 0) {
                    System.out.println("You have no existing fluid entries to delete.");
                } else {
                    fluid.deleteFluid(inputArguments);
                }
            } else {
                System.out.println("Please enter in the format: deletefluid [entry_number]");
            }
            break;
        case Keywords.LIST_DRINKS:
            fluid.listFluid();
            break;
        case Keywords.INPUT_ADD_WEIGHT:
            try {
                weightTracker.readInput(input);
            } catch (DukeException e) {
                return;
            } catch (DateTimeParseException e) {
                weightTracker.printAddWeightException();
            }
            break;
        case Keywords.INPUT_DELETE_WEIGHT:
            try {
                weightTracker.readInput(input);
            } catch (DukeException e) {
                return;
            }
            break;
        case Keywords.INPUT_CHECK_WEIGHT:
            try {
                weightTracker.readInput(input);
            } catch (DukeException e) {
                return;
            }
            weightTracker.readInput(input);
            break;
        case Keywords.INPUT_HELP:
            UserHelp.generateUserHelpParameters(inputArguments);
            break;
        case Keywords.INPUT_BYE:
            isExit = true;
            System.out.println(CREDITS);
            break;
        default:
            System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            break;
        }
    }
}