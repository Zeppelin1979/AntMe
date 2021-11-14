package AntMe.PlayerManagement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import AntMe.Simulation.PlayerInfo;
    /// <summary>
    /// Class, to extract PlayerInfos from given Ai-Assembly.
    /// </summary>
    /// <author>Tom Wendel (tom@antme.net)</author>

public class AiAnalysis {

    /// <summary>
    /// Analyzes a Ai-File based on filename.
    /// </summary>
    /// <param name="filename">Ai-File to analyze.</param>
    /// <returns>List of found PlayerInfos</returns>
    /// <throws><see cref="ArgumentException"/></throws>
    /// <throws><see cref="ArgumentNullException"/></throws>
    /// <throws><see cref="PathTooLongException"/></throws>
    /// <throws><see cref="DirectoryNotFoundException"/></throws>
    /// <throws><see cref="IOException"/></throws>
    /// <throws><see cref="UnauthorizedAccessException"/></throws>
    /// <throws><see cref="FileNotFoundException"/></throws>
    /// <throws><see cref="NotSupportedException"/></throws>
    /// <throws><see cref="SecurityException"/></throws>
    /// <throws><see cref="FileLoadException"/></throws>
    /// <throws><see cref="BadImageFormatException"/></throws>
    /// <throws><see cref="RuleViolationException"/></throws>
    public static ArrayList<PlayerInfo> analyse(String filename)
    {
        return analyse(filename, true);
    }

    /// <summary>
    /// Analyzes a Ai-File based on filename.
    /// </summary>
    /// <param name="filename">Ai-File to analyze.</param>
    /// <param name="checkRules">True, if Analyser should also check player-rules.</param>
    /// <returns>List of found PlayerInfos</returns>
    /// <throws><see cref="ArgumentException"/></throws>
    /// <throws><see cref="ArgumentNullException"/></throws>
    /// <throws><see cref="PathTooLongException"/></throws>
    /// <throws><see cref="DirectoryNotFoundException"/></throws>
    /// <throws><see cref="IOException"/></throws>
    /// <throws><see cref="UnauthorizedAccessException"/></throws>
    /// <throws><see cref="FileNotFoundException"/></throws>
    /// <throws><see cref="NotSupportedException"/></throws>
    /// <throws><see cref="SecurityException"/></throws>
    /// <throws><see cref="FileLoadException"/></throws>
    /// <throws><see cref="BadImageFormatException"/></throws>
    /// <throws><see cref="RuleViolationException"/></throws>
    public static ArrayList<PlayerInfo> analyse(String filename, boolean checkRules)
    {
        FileInputStream input;
        try {
            input = new FileInputStream(filename);
            ArrayList<PlayerInfo> array = analyse(input.readAllBytes(), checkRules);
            input.close();
            return array;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
                // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /// <summary>
    /// Analyzes a Ai-File based on binary file-dump.
    /// </summary>
    /// <param name="file">Ai-File to analyze.</param>
    /// <returns>List of found PlayerInfos</returns>
    /// <throws><see cref="FileLoadException"/></throws>
    /// <throws><see cref="BadImageFormatException"/></throws>
    /// <throws><see cref="ArgumentNullException"/></throws>
    /// <throws><see cref="FileNotFoundException"/></throws>
    /// <throws><see cref="RuleViolationException"/></throws>
    public static ArrayList<PlayerInfo> analyse(byte[] file)
    {
        return analyse(file, true);
    }

    /// <summary>
    /// Analyzes a Ai-File based on binary file-dump.
    /// </summary>
    /// <param name="file">Ai-File to analyze.</param>
    /// <param name="checkRules">True, if Analyser should also check player-rules.</param>
    /// <returns>List of found PlayerInfos</returns>
    /// <throws><see cref="FileLoadException"/></throws>
    /// <throws><see cref="BadImageFormatException"/></throws>
    /// <throws><see cref="ArgumentNullException"/></throws>
    /// <throws><see cref="FileNotFoundException"/></throws>
    /// <throws><see cref="RuleViolationException"/></throws>
    public static ArrayList<PlayerInfo> analyse(byte[] file, boolean checkRules)
    {
        // setup appdomain
        AppDomainSetup setup = new AppDomainSetup();

        // Base Path for references
        string applicationBase = AppDomain.CurrentDomain.RelativeSearchPath;
        if (string.IsNullOrEmpty(applicationBase))
            applicationBase = AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
        setup.ApplicationBase = applicationBase;

        // setup accessrights for the appdomain
        PermissionSet rechte = new PermissionSet(PermissionState.None);
        rechte.AddPermission(new SecurityPermission(SecurityPermissionFlag.Execution));
        rechte.AddPermission(new ReflectionPermission(ReflectionPermissionFlag.MemberAccess));

        // create appdomain and analyse-host
        AppDomain app = AppDomain.CreateDomain("AnalysisHost", AppDomain.CurrentDomain.Evidence, setup, rechte);
        //app.ReflectionOnlyAssemblyResolve +=
        //    delegate(object sender, ResolveEventArgs args) { return Assembly.ReflectionOnlyLoad(args.Name); };

        AnalysisHost host =
            (AnalysisHost)
            app.CreateInstanceAndUnwrap(
                Assembly.GetExecutingAssembly().FullName, "AntMe.Simulation.AnalysisHost");
        List<PlayerInfo> spieler = host.Analyse(file, checkRules);

        // in case of exceptions unload the appdomain and throw the exception
        if (spieler == null)
        {
            Exception ex = host.Exception;
            AppDomain.Unload(app);
            throw ex;
        }

        // unload appdomain
        AppDomain.Unload(app);

        // return list of PlayerInfo
        return spieler;
    }

    /// <summary>
    /// Find a specific Player-information in given Ai-File.
    /// </summary>
    /// <param name="file">File as binary file-dump</param>
    /// <param name="className">Class-name of Ai</param>
    /// <returns>the right <see cref="PlayerInfo"/></returns>
    /// <throws><see cref="NotSupportedException"/></throws>
    public static PlayerInfoFiledump findPlayerInformation(byte[] file, String className)
    {
        // load all included players
        List<PlayerInfo> foundPlayers = analyse(file);

        // If there is no classname, just take the only one
        if (className == string.Empty)
        {
            if (foundPlayers.Count == 1)
            {
                return new PlayerInfoFiledump(foundPlayers[0], file);
            }
            throw new InvalidOperationException(Resource.SimulationCoreAnalysisNoClassFound);
        }

        // search for needed classname
        for (PlayerInfo player : foundPlayers)
        {
            if (player.ClassName == className)
            {
                return new PlayerInfoFiledump(player, file);
            }
        }

        // Exception, if there was no hit
        throw new InvalidOperationException(Resource.SimulationCoreAnalysisNoClassFound);
    }

    /// <summary>
    /// Find a specific <see cref="PlayerInfo"/> in given Ai-File.
    /// </summary>
    /// <param name="file">File as filename</param>
    /// <param name="className">Class-name of Ai</param>
    /// <returns>the right <see cref="PlayerInfo"/> or null for no hits</returns>
    public static PlayerInfoFilename findPlayerInformation(string file, string className)
    {
        // load all included players
        List<PlayerInfo> foundPlayer = Analyse(file);

        // If there is no classname, just take the only one
        if (className == string.Empty)
        {
            if (foundPlayer.Count == 1)
            {
                return new PlayerInfoFilename(foundPlayer[0], file);
            }
            throw new InvalidOperationException(Resource.SimulationCoreAnalysisNoClassFound);
        }

        // search for needed classname
        foreach (PlayerInfo player in foundPlayer)
        {
            if (player.ClassName == className)
            {
                return new PlayerInfoFilename(player, file);
            }
        }

        // Exception, if there was no hit
        throw new InvalidOperationException(Resource.SimulationCoreAnalysisNoClassFound);
    }
}
