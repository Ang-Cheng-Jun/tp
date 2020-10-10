package seedu.duke.commands;

import seedu.duke.common.Messages;
import seedu.duke.data.AirConditioner;
import seedu.duke.data.Fan;
import seedu.duke.data.Lights;
import seedu.duke.data.WaterHeater;
import seedu.duke.exceptions.InvalidAdditionOfAppliance;

import static seedu.duke.common.Messages.MESSAGE_APPLIANCE_EXIST;
import static seedu.duke.common.Messages.MESSAGE_APPLIANCE_TYPE_NOT_EXIST;


public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a new appliance to the particular location to the SmartHomeBot. \n"
            + "Parameters: add NAME l/LOCATION w/WATTS t/TYPE_OF_APPLIANCE \n" + "Example: "
            + COMMAND_WORD + " Fan1 l/Bedroom 1 w/50 t/Fan";

    private final String name;
    private final String location;
    private final String power;
    private final String type;

    public AddCommand(String name, String location, String power, String type) {
        this.name = name;
        this.location = location;
        this.power = power;
        this.type = type;
    }

    @Override
    public void execute() {
        if (homeLocationsList.isLocationCreated(location)) {
            try {
                switch (type.toLowerCase()) {
                case Fan.TYPE_WORD:
                    Fan fan = new Fan(name, location, power);
                    appliances.addAppliance(fan);
                    break;
                case AirConditioner.TYPE_WORD:
                    AirConditioner ac = new AirConditioner(name, location, power);
                    appliances.addAppliance(ac);
                    break;
                case Lights.TYPE_WORD:
                    Lights light = new Lights(name, location, power);
                    appliances.addAppliance(light);
                    break;
                case WaterHeater.TYPE_WORD:
                    WaterHeater waterheater = new WaterHeater(name, location, power);
                    appliances.addAppliance(waterheater);
                    break;
                default:
                    ui.showToUser(MESSAGE_APPLIANCE_TYPE_NOT_EXIST);
                }
            } catch (InvalidAdditionOfAppliance e) {
                ui.showToUser(MESSAGE_APPLIANCE_EXIST);
            }

        } else {
            ui.showToUser(Messages.MESSAGE_LOCATION_NOT_EXIST);
        }
    }

}