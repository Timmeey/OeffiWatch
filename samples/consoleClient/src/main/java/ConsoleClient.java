

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.timmeey.oeffiwatch.Grabber;
import de.timmeey.oeffiwatch.exception.AmbigiuousStationNameException;
import de.timmeey.oeffiwatch.station.Station;

public class ConsoleClient {

	public static void main(String[] args) throws IOException, AmbigiuousStationNameException,
	      ParseException, InterruptedException, org.apache.commons.cli.ParseException {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setDescPadding(6);
		formatter.setLongOptPrefix("   --");

		Options options = setupOptions();

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("v")){
		        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
			}
			String stationName = cmd.getOptionValue("station");

			try {
				Grabber grabber = Grabber.getVBBInstance();
				Station station = grabber.getStation(stationName);

				if (cmd.hasOption("J")) {
					System.out.println(station.toJson(true)); // NOSONAR
				} else {
					System.out.println(station.toJson(false)); // NOSONAR
				}
			} catch (AmbigiuousStationNameException e) {
				System.out.println(e.toJson()); // NOSONAR
			} catch (de.timmeey.oeffiwatch.exception.ParseException e) {
				System.err.println("Something went wrong parsing the Website");
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		} catch (org.apache.commons.cli.MissingArgumentException e) {
			formatter.printHelp("ÖffiGrabber", e.getMessage(), options, "", false);
		} catch (org.apache.commons.cli.MissingOptionException e) {
			formatter.printHelp("ÖffiGrabber", e.getMessage(), options, "", false);
		} catch (org.apache.commons.cli.ParseException e) {
			formatter.printHelp("ÖffiGrabber", e.getMessage(), options, "", false);
		}

	}

	private static Options setupOptions() {
		Options options = new Options();
		OptionGroup outputType = new OptionGroup();
		outputType.addOption(Option.builder("j").longOpt("json")
		      .desc("Specifies the output to be in compact json").numberOfArgs(0).build());
		outputType.addOption(Option.builder("J").longOpt("prettyJson")
		      .desc("Specifies the output to be in prettyPrinted json").numberOfArgs(0).build());
		outputType.addOption(Option.builder("h").longOpt("humanReadable")
		      .desc("Specifies the output to be in human readable form").numberOfArgs(0).build());
		outputType.setRequired(true);
		options.addOptionGroup(outputType);
		

		options.addOption(Option.builder("s").longOpt("station")
		      .desc("Specifies the Station to query").numberOfArgs(1).hasArg().required().build());
		
		options.addOption(Option.builder("v").longOpt("verbose").hasArg(false).desc("Set verbose").build());
		// options.addOption(Option.builder().argName("f").longOpt("filter")
		// .desc("Comma seperated list of Vehicle filters to
		// apply").optionalArg(true)
		// .valueSeparator(',').hasArg().build());

		return options;
	}

}
