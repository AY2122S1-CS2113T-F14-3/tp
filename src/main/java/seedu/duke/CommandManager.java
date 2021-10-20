package seedu.duke;

import seedu.duke.exceptions.DukeException;
import seedu.duke.exceptions.FoodBankException;
import seedu.duke.exceptions.MealException;
import seedu.duke.exceptions.FluidExceptions;
import seedu.duke.gym.ScheduleTracker;
import seedu.duke.gym.WorkoutTracker;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

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
    protected Storage storage;

    public CommandManager(Storage storage, Fluid fluid, Meal meal, ScheduleTracker scheduleTracker, WorkoutTracker workoutTracker,
                          WeightTracker weightTracker, UserHelp userHelp) {
        this.fluid = fluid;
        this.meal = meal;
        this.scheduleTracker = scheduleTracker;
        this.workoutTracker = workoutTracker;
        this.scanner = new Scanner(System.in);
        this.weightTracker = weightTracker;
        this.userHelp = userHelp;
        this.isExit = false;
        this.storage = storage;
    }

    public void commandChecker() throws DukeException, NullPointerException, MealException, FluidExceptions, FoodBankException, IOException {
        String input = scanner.nextLine();
        System.out.println(Ui.HORIZONTAL_BAR + System.lineSeparator());
        String[] splitResults = input.trim().split(" ", 2);
        command = splitResults[0];
        inputArguments = (splitResults.length == 2) ? splitResults[1] : null;
        assert !Objects.equals(inputArguments, "");
        switch (command) {
        case Keywords.LIBRARY:
            assert inputArguments != null;
            foodBankParser(inputArguments);
            break;
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
        case Keywords.INPUT_DELETE_WORKOUT:
        case Keywords.INPUT_LIST_WORKOUT:
            executeWorkoutCommand(command, inputArguments);
            break;
        case Keywords.INPUT_ADD_SCHEDULE:
        case Keywords.INPUT_DELETE_SCHEDULE:
        case Keywords.INPUT_LIST_SCHEDULE:
            executeScheduleCommand(command, inputArguments);
            break;
        case Keywords.INPUT_DRINKS:
            if (inputArguments != null) {
                try {
                    fluid.addFluid(inputArguments);
                } catch (FluidExceptions | FoodBankException e) {
                    System.out.println(ClickfitMessages.FLUID_ADD_FORMAT_ERROR);
                }
            } else {
                throw new FluidExceptions();
            }
            break;
        case Keywords.DELETE_DRINKS:
            if (inputArguments != null) {
                if (fluid.fluidArray.size() == 0) {
                    System.out.println(ClickfitMessages.FLUID_DELETE_ERROR);
                } else {
                    fluid.deleteFluid(inputArguments);
                }
            } else {
                System.out.println(ClickfitMessages.FLUID_DELETE_FORMAT_ERROR);
            }
            break;
        case Keywords.LIST_DRINKS:
            if (fluid.fluidArray.size() == 0) {
                System.out.println(ClickfitMessages.FLUID_LIST_ERROR);
            } else {
                fluid.listFluid();
            }
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
        case Keywords.INPUT_CHECK_WEIGHT:
            try {
                weightTracker.readInput(input);
            } catch (DukeException e) {
                return;
            }
            break;
        case Keywords.INPUT_HELP:
            UserHelp.generateUserHelpParameters(inputArguments);
            break;
        case Keywords.INPUT_BYE:
            isExit = true;
            System.out.println(ClickfitMessages.CREDITS);
            break;
        default:
            System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            break;
        }
        storage.saveAllTasks(fluid, meal, weightTracker);
    }

    public void foodBankParser(String inputArguments) throws NullPointerException, FoodBankException {
        String[] splitResults = inputArguments.trim().split(" ", 2);
        command = splitResults[0];
        inputArguments = (splitResults.length == 2) ? splitResults[1] : null;
        switch (command) {
        case Keywords.ADD_FLUID:
            FoodBank.addCustomFluid(inputArguments);
            break;
        case Keywords.DELETE_DRINKS:
            FoodBank.deleteCustomFluids(inputArguments);
            break;
        case Keywords.LIST_DRINKS:
            FoodBank.listCustomFluids();
            break;
        case Keywords.ADD_MEAL:
            FoodBank.addCustomMeal(inputArguments);
            break;
        case Keywords.DELETE_MEAL:
            FoodBank.deleteCustomMeal(inputArguments);
            break;
        case Keywords.LIST_MEAL:
            FoodBank.listCustomMeal();
            break;
        default:
            System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            break;
        }
    }

    public void executeScheduleCommand(String command, String inputArguments) throws DukeException {
        switch (command) {
        case Keywords.INPUT_ADD_SCHEDULE:
            scheduleTracker.addScheduledWorkout(inputArguments, false);
            break;
        case Keywords.INPUT_DELETE_SCHEDULE:
            scheduleTracker.deleteScheduledWorkout(inputArguments);
            break;
        case Keywords.INPUT_LIST_SCHEDULE:
            scheduleTracker.listScheduledWorkouts(inputArguments);
            break;
        default:
            System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            break;
        }
        Storage.saveScheduleData(scheduleTracker);
    }

    public void executeWorkoutCommand(String command, String inputArguments) throws DukeException {
        switch (command) {
        case Keywords.INPUT_ADD_WORKOUT:
            workoutTracker.addWorkout(inputArguments, false);
            break;
        case Keywords.INPUT_DELETE_WORKOUT:
            workoutTracker.deleteWorkout(inputArguments);
            break;
        case Keywords.INPUT_LIST_WORKOUT:
            workoutTracker.listWorkouts(inputArguments);
            break;
        default:
            System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            break;
        }
        Storage.saveWorkoutData(workoutTracker);
    }
}