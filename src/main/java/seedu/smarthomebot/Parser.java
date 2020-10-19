package seedu.smarthomebot;

import seedu.smarthomebot.commands.AddCommand;
import seedu.smarthomebot.commands.Command;
import seedu.smarthomebot.commands.CreateCommand;
import seedu.smarthomebot.commands.DeleteCommand;
import seedu.smarthomebot.commands.ExitCommand;
import seedu.smarthomebot.commands.HelpCommand;
import seedu.smarthomebot.commands.InvalidCommand;
import seedu.smarthomebot.commands.ListCommand;
import seedu.smarthomebot.commands.OffCommand;
import seedu.smarthomebot.commands.OnCommand;
import seedu.smarthomebot.commands.RemoveCommand;
import seedu.smarthomebot.commands.UsageCommand;
import seedu.smarthomebot.exceptions.EmptyParameterException;
import seedu.smarthomebot.exceptions.InvalidAddCommand;
import seedu.smarthomebot.exceptions.InvalidFomartException;
import seedu.smarthomebot.exceptions.InvalidValue;
import seedu.smarthomebot.exceptions.PowerValueExceed;

import static seedu.smarthomebot.common.Messages.MESSAGE_INVALID_ADD_COMMAND;
import static seedu.smarthomebot.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.smarthomebot.common.Messages.MESSAGE_INVALID_LIST_COMMAND;
import static seedu.smarthomebot.common.Messages.MESSAGE_POWER_EXCEEDED;
import static seedu.smarthomebot.common.Messages.MESSAGE_POWER_NOT_NUMBER;


public class Parser {

    private static final String APPLIANCE_TYPE = "appliance";
    private static final String LOCATION_TYPE = "location";

    private static Command prepareOnCommand(String arguments) {
        int indexParameter = arguments.indexOf("p/");
        String name;
        String parameter;
        String type;
        try {
            if (arguments.substring(0,2).equals("n/")) {
                type = APPLIANCE_TYPE;
                if (indexParameter < 1) {
                    name = arguments.substring(2).trim();
                    if (checkForEmptyInput(name)) {
                        throw new EmptyParameterException();
                    }
                    parameter = "";
                } else {
                    name = arguments.substring(2,indexParameter).trim();
                    parameter = arguments.substring(indexParameter + 2).toLowerCase().trim();
                    if (checkForEmptyInput(name)
                            || checkForEmptyInput(parameter)) {
                        throw new EmptyParameterException();
                    }
                    convertParameterToInt(parameter);
                }
                return new OnCommand(name, type, parameter);
            } else if (arguments.substring(0,2).equals("l/")) {
                name = arguments.substring(2);
                type = LOCATION_TYPE;
                if (checkForEmptyInput(name)) {
                    throw new EmptyParameterException();
                }
                return new OnCommand(name, type, "0");
            } else {
                throw new InvalidFomartException();
            }

        } catch (EmptyParameterException e) {
            return new InvalidCommand("Empty Appliance Name");

        } catch (InvalidValue e) {
            return new InvalidCommand(MESSAGE_POWER_NOT_NUMBER);

        } catch (InvalidFomartException e) {
            return new InvalidCommand("Invalid Format");

        }

    }

    private static Command prepareOffCommand(String arguments) {
        String name;
        String type;
        try {
            name = arguments.substring(2);
            if (checkForEmptyInput(name)) {
                throw new EmptyParameterException();
            }
            if (arguments.substring(0,2).equals("n/")) {
                type = APPLIANCE_TYPE;
                return new OffCommand(name, type);
            } else if (arguments.substring(0,2).equals("l/")) {
                type = LOCATION_TYPE;
                return new OffCommand(name, type);
            } else {
                throw new InvalidFomartException();
            }

        } catch (EmptyParameterException e) {
            return new InvalidCommand("Empty Appliance Name");

        } catch (InvalidFomartException e) {
            return new InvalidCommand("Invalid Format");

        }

    }

    private static void convertParameterToInt(String parameter) throws InvalidValue {
        try {
            Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            throw new InvalidValue();
        }

    }

    private static Command prepareAddCommand(String arguments) {
        int indexLocation = arguments.indexOf("l/");
        int indexPower = arguments.indexOf("w/");
        int indexType = arguments.indexOf("t/");
        String name;
        String location;
        String power;
        String type;

        try {
            if (indexLocation < indexPower && indexPower < indexType) {
                name = arguments.substring(0, indexLocation).trim();
                location = arguments.substring(indexLocation + 2, indexPower).trim();
                power = arguments.substring(indexPower + 2, indexType).trim();
                type = arguments.substring(indexType + 2).toLowerCase().trim();

                if (checkForEmptyInput(name) | checkForEmptyInput(location)
                        | checkForEmptyInput(power) | checkForEmptyInput(type)) {
                    throw new InvalidAddCommand();
                }

            } else {
                throw new InvalidAddCommand();
            }

            testPowerValidity(power);
            return new AddCommand(name, location, power, type);

        } catch (InvalidAddCommand e) {
            return new InvalidCommand(MESSAGE_INVALID_ADD_COMMAND);

        } catch (InvalidValue e) {
            return new InvalidCommand(MESSAGE_POWER_NOT_NUMBER);

        } catch (PowerValueExceed e) {
            return new InvalidCommand(MESSAGE_POWER_EXCEEDED);
        }

    }

    private static Command prepareListCommand(String arguments) {
        String str[] = arguments.split(" ");
        if (str[0].equals(LOCATION_TYPE)) {
            return new ListCommand(LOCATION_TYPE, "");
        } else if (str[0].equals(APPLIANCE_TYPE)) {
            if (arguments.equals(APPLIANCE_TYPE)) {
                return new ListCommand(APPLIANCE_TYPE, "");
            } else if (str[1].trim().substring(0,2).equals("l/")) {
                return new ListCommand(APPLIANCE_TYPE, str[1].trim().substring(2));
            } else {
                return new InvalidCommand("Invalid list appliance format");
            }
        } else {
            return new InvalidCommand(MESSAGE_INVALID_LIST_COMMAND);
        }
    }

    private Command prepareCreateCommand(String arguments) {
        if (!checkForEmptyInput(arguments)) {
            return new CreateCommand(arguments);
        } else {
            // Might need to print out a better message
            return new InvalidCommand(MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }

    private static void testPowerValidity(String power) throws PowerValueExceed, InvalidValue {
        try {
            int powerValue = Integer.parseInt(power);
            // Common appliance should not exceed 9999 watts
            if ((powerValue < 1) || (powerValue > 9999)) {
                throw new PowerValueExceed();
            }
        } catch (NumberFormatException e) {
            throw new InvalidValue();
        }
    }

    private static boolean checkForEmptyInput(String input) {
        return (input.isEmpty());
    }

    public Command parseCommand(String userInput) {
        String[] words = userInput.trim().split(" ", 2);
        final String commandWord = words[0];
        final String arguments = userInput.replaceFirst(commandWord, "").trim();

        switch (commandWord) {
        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        case CreateCommand.COMMAND_WORD:
            return prepareCreateCommand(arguments);
        case RemoveCommand.COMMAND_WORD:
            return new RemoveCommand(arguments);
        case AddCommand.COMMAND_WORD:
            return prepareAddCommand(arguments);
        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommand(arguments);
        case OnCommand.COMMAND_WORD:
            return prepareOnCommand(arguments);
        case OffCommand.COMMAND_WORD:
            return prepareOffCommand(arguments);
        case ListCommand.COMMAND_WORD:
            return prepareListCommand(arguments);
        case UsageCommand.COMMAND_WORD:
            return new UsageCommand();
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();
        default:
            return new InvalidCommand(MESSAGE_INVALID_COMMAND_FORMAT);
        }

    }

}

