package AntMe.PlayerManagement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import AntMe.Simulation.ConfigurationErrorsException;
import AntMe.Simulation.PlayerInfo;
import AntMe.Simulation.SimulationSettings;

/// <summary>
/// Host-Class for Ai-Analysis to host the analyze-core inside application-domains.
/// </summary>
public class AnalysisHost {

    /// <summary>
    /// Type of baseAnt from core.
    /// </summary>
    private Type coreAnt;

    /// <summary>
    /// Holds all thrown exceptions.
    /// </summary>
    private Exception exception;

    /// <summary>
    /// List of all Ant-BaseClasses in every known language.
    /// </summary>
    private HashMap<PlayerLanguages, Type> languageBases;

    /// <summary>
    /// Sets special simulation-settings.
    /// </summary>
    /// <param name="settings">special settings</param>
    public void initSettings(SimulationSettings settings) throws ConfigurationErrorsException
    {
        SimulationSettings.setCustomSettings(settings);
    }

    /// <summary>
    /// Searches in the given file for possible PlayerInfos and delivers a list of found Players.
    /// </summary>
    /// <param name="file">Ai-File as binary file-dump.</param>
    /// <param name="checkRules">True, if Analyzer should also check player-rules</param>
    /// <returns>List of found players.</returns>
    public ArrayList<PlayerInfo> analyse(byte[] file, boolean checkRules)
    {
        try
        {
            // load base-class from Simulation-Core.
            Assembly simulation = Assembly.Load(Assembly.GetExecutingAssembly().FullName);
            coreAnt = AntMe.Simulation.CoreAnt.GetType();
            simulation.GetType("AntMe.Simulation.CoreAnt");

            // load all base-classes of different languages
            languageBases = new HashMap<PlayerLanguages, Type>();
            languageBases.put(
                PlayerLanguages.Deutsch,
                simulation.GetType("AntMe.Deutsch.Basisameise"));
            languageBases.put(
                PlayerLanguages.English,
                simulation.GetType("AntMe.English.BaseAnt"));

            // open Ai-File
            exception = null;
            Assembly assembly = Assembly.Load(file);

            return analyseAssembly(assembly, checkRules);
        }
        catch (Exception ex)
        {
            // problems during analyse - save Exception without throwing and return null
            exception = ex;
            return null;
        }
    }

    /// <summary>
    /// Analyzes the given for any kind of valid player-information.
    /// </summary>
    /// <param name="assembly">given assembly.</param>
    /// <param name="checkRules">true, if the Analyser should also check player-rules</param>
    /// <returns>List of valid players.</returns>
    private ArrayList<PlayerInfo> analyseAssembly(Assembly assembly, bool checkRules)
    {
        // All included players would be marked as static, if one static play is found.
        bool staticVariables = false;

        bool foreignReferences = false;
        string referenceInformation = string.Empty;

        List<PlayerInfo> players = new List<PlayerInfo>();

        // filter valid references
        AssemblyName[] references = assembly.GetReferencedAssemblies();
        foreach (AssemblyName reference in references)
        {
            switch (reference.Name)
            {
                case "mscorlib":
                    // Framework-Core
                    byte[] coreschl�ssel = new AssemblyName(typeof(object).Assembly.FullName).GetPublicKeyToken();
                    byte[] referenzschl�ssel = reference.GetPublicKeyToken();
                    if (coreschl�ssel[0] != referenzschl�ssel[0] ||
                        coreschl�ssel[1] != referenzschl�ssel[1] ||
                        coreschl�ssel[2] != referenzschl�ssel[2] ||
                        coreschl�ssel[3] != referenzschl�ssel[3] ||
                        coreschl�ssel[4] != referenzschl�ssel[4] ||
                        coreschl�ssel[5] != referenzschl�ssel[5] ||
                        coreschl�ssel[6] != referenzschl�ssel[6] ||
                        coreschl�ssel[7] != referenzschl�ssel[7])
                    {
                        throw new RuleViolationException(Resource.SimulationCoreAnalysisCheatWithFxFake);
                    }
                    break;
                case "Microsoft.VisualBasic":
                    // TODO: Pr�fen, wie wir damit umgehen
                    break;
                case "System":
                    // TODO: Pr�fen, wie wir damit umgehen
                    break;
                case "AntMe.Simulation":
                    if (Assembly.GetCallingAssembly().FullName != reference.FullName)
                        throw new RuleViolationException(Resource.SimulationCoreAnalysisNotSupportedAntVersion);

                    //if (reference.Version.Major == 1) {
                    //    switch (reference.Version.Minor) {
                    //        case 1:
                    //            // Version 1.1
                    //            throw new NotSupportedException(
                    //                Resource.SimulationCoreAnalysisNotSupportedAntVersion);
                    //        case 5:
                    //            // Version 1.5
                    //            try {
                    //                Assembly.ReflectionOnlyLoad("AntMe.Simulation.Ameise");
                    //            }
                    //            catch (Exception ex) {
                    //                throw new DllNotFoundException(
                    //                    string.Format(
                    //                        Resource.SimulationCoreAnalysisOldAssemblyLoadFailed,
                    //                        reference.Version.ToString(2)),
                    //                    ex);
                    //            }
                    //            break;
                    //        case 6:
                    //            if (Assembly.GetCallingAssembly().FullName != reference.FullName) {
                    //                throw new RuleViolationException(Resource.SimulationCoreAnalysisNewerVersion);
                    //            }
                    //            break;
                    //        default:
                    //            // unknown Version
                    //            throw new NotSupportedException(
                    //                string.Format(
                    //                    Resource.SimulationCoreAnalysisUnknownVersion, reference.Version.ToString(2)));
                    //    }
                    //}
                    //else {
                    //    // unknown Version
                    //    throw new NotSupportedException(
                    //        string.Format(
                    //            Resource.SimulationCoreAnalysisUnknownVersion, reference.Version.ToString(2)));
                    //}
                    break;
                default:
                    // load unknown refereneces and add to list
                    try
                    {
                        Assembly.ReflectionOnlyLoad(reference.FullName);
                    }
                    catch (Exception ex)
                    {
                        throw new DllNotFoundException(
                            string.Format(
                                Resource.SimulationCoreAnalysisUnknownReferenceLoadFailed, reference.FullName),
                            ex);
                    }
                    foreignReferences = true;
                    referenceInformation += reference.FullName + Environment.NewLine;
                    break;
            }
        }

        // TODO: Statische Variablen finden
        // TODO: Statische Konstruktoren finden - immernoch ein Problem?
        // TODO: Nested Types!

        Type[] types = assembly.GetTypes();
        foreach (Type type in types)
        {
            // Suche nach statischen Variablen und bestimme so, ob der Spieler
            // ein globales Ged�chtnis f�r seine Ameisen benutzt.
            BindingFlags flags =
                BindingFlags.Public | BindingFlags.NonPublic |
                BindingFlags.Static | BindingFlags.SetField;
            staticVariables |= type.GetFields(flags).Length > 0;
        }

        // Gefundene KIs auf Regeln pr�fen
        // Betrachte alle �ffentlichen Typen in der Bibliothek.
        foreach (Type type in assembly.GetExportedTypes())
        {
            // Pr�fe ob der Typ von der Klasse Ameise erbt.

            // Ameisenversion 1.6
            if (type.IsSubclassOf(coreAnt))
            {
                // Leerer Spieler-Rumpf
                int playerDefinitions = 0;

                PlayerInfo player = new PlayerInfo();
                player.SimulationVersion = PlayerSimulationVersions.Version_1_7;
                player.ClassName = type.FullName;

                // Sprache ermitteln
                foreach (PlayerLanguages sprache in languageBases.Keys)
                {
                    if (type.IsSubclassOf(languageBases[sprache]))
                    {
                        player.Language = sprache;
                        break;
                    }
                }
                // TODO: Vorgehensweise bei unbekannten Sprachen

                // Attribute auslesen
                foreach (CustomAttributeData attribute in CustomAttributeData.GetCustomAttributes(type))
                {
                    // Player-Attribut auslesen
                    switch (attribute.Constructor.ReflectedType.FullName)
                    {
                        case "AntMe.Deutsch.SpielerAttribute":
                            playerDefinitions++;
                            foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                            {
                                switch (argument.MemberInfo.Name)
                                {
                                    case "Volkname":
                                        player.ColonyName = (string)argument.TypedValue.Value;
                                        break;
                                    case "Vorname":
                                        player.FirstName = (string)argument.TypedValue.Value;
                                        break;
                                    case "Nachname":
                                        player.LastName = (string)argument.TypedValue.Value;
                                        break;
                                }
                            }
                            break;
                        case "AntMe.English.PlayerAttribute":
                            playerDefinitions++;
                            foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                            {
                                switch (argument.MemberInfo.Name)
                                {
                                    case "ColonyName":
                                        player.ColonyName = (string)argument.TypedValue.Value;
                                        break;
                                    case "FirstName":
                                        player.FirstName = (string)argument.TypedValue.Value;
                                        break;
                                    case "LastName":
                                        player.LastName = (string)argument.TypedValue.Value;
                                        break;
                                }
                            }
                            break;
                    }

                    // Caste-Attribut auslesen
                    CasteInfo caste = new CasteInfo();
                    switch (attribute.Constructor.ReflectedType.FullName)
                    {
                        case "AntMe.English.CasteAttribute":

                            // englische Kasten
                            foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                            {
                                switch (argument.MemberInfo.Name)
                                {
                                    case "Name":
                                        caste.Name = (string)argument.TypedValue.Value;
                                        break;
                                    case "SpeedModifier":
                                        caste.Speed = (int)argument.TypedValue.Value;
                                        break;
                                    case "RotationSpeedModifier":
                                        caste.RotationSpeed = (int)argument.TypedValue.Value;
                                        break;
                                    case "LoadModifier":
                                        caste.Load = (int)argument.TypedValue.Value;
                                        break;
                                    case "RangeModifier":
                                        caste.Range = (int)argument.TypedValue.Value;
                                        break;
                                    case "ViewRangeModifier":
                                        caste.ViewRange = (int)argument.TypedValue.Value;
                                        break;
                                    case "EnergyModifier":
                                        caste.Energy = (int)argument.TypedValue.Value;
                                        break;
                                    case "AttackModifier":
                                        caste.Attack = (int)argument.TypedValue.Value;
                                        break;
                                }
                            }

                            // Bei Individualkasten zur Liste 
                            if (!caste.IsEmpty())
                            {
                                player.Castes.Add(caste);
                            }
                            break;
                        case "AntMe.Deutsch.KasteAttribute":

                            // deutsche Kasten
                            foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                            {
                                switch (argument.MemberInfo.Name)
                                {
                                    case "Name":
                                        caste.Name = (string)argument.TypedValue.Value;
                                        break;
                                    case "GeschwindigkeitModifikator":
                                        caste.Speed = (int)argument.TypedValue.Value;
                                        break;
                                    case "DrehgeschwindigkeitModifikator":
                                        caste.RotationSpeed = (int)argument.TypedValue.Value;
                                        break;
                                    case "LastModifikator":
                                        caste.Load = (int)argument.TypedValue.Value;
                                        break;
                                    case "ReichweiteModifikator":
                                        caste.Range = (int)argument.TypedValue.Value;
                                        break;
                                    case "SichtweiteModifikator":
                                        caste.ViewRange = (int)argument.TypedValue.Value;
                                        break;
                                    case "EnergieModifikator":
                                        caste.Energy = (int)argument.TypedValue.Value;
                                        break;
                                    case "AngriffModifikator":
                                        caste.Attack = (int)argument.TypedValue.Value;
                                        break;
                                }
                            }

                            // Bei Individualkasten zur Liste 
                            if (!caste.IsEmpty())
                            {
                                player.Castes.Add(caste);
                            }
                            break;
                    }

                    // Access-Attributes
                    bool isAccessAttribute = false;
                    // TODO: Request-Infos lokalisieren
                    switch (attribute.Constructor.ReflectedType.FullName)
                    {
                        case "AntMe.Deutsch.DateizugriffAttribute":
                            player.RequestFileAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Dateizugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.Deutsch.DatenbankzugriffAttribute":
                            player.RequestDatabaseAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Datenbankzugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.Deutsch.FensterzugriffAttribute":
                            player.RequestUserInterfaceAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Fensterzugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.Deutsch.NetzwerkzugriffAttribute":
                            player.RequestNetworkAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Netzwerkzugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.English.FileAccessAttribute":
                            player.RequestFileAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Dateizugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.English.DatabaseAccessAttribute":
                            player.RequestDatabaseAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Datenbankzugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.English.UserinterfaceAccessAttribute":
                            player.RequestUserInterfaceAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Fensterzugriff:" + Environment.NewLine;
                            break;
                        case "AntMe.English.NetworkAccessAttribute":
                            player.RequestNetworkAccess = true;
                            isAccessAttribute = true;
                            player.RequestInformation += "Netzwerkzugriff:" + Environment.NewLine;
                            break;
                    }

                    if (isAccessAttribute)
                    {
                        foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                        {
                            switch (argument.MemberInfo.Name)
                            {
                                case "Beschreibung":
                                    player.RequestInformation += argument.TypedValue.Value +
                                        Environment.NewLine + Environment.NewLine;
                                    break;
                                case "Description":
                                    player.RequestInformation += argument.TypedValue.Value +
                                        Environment.NewLine + Environment.NewLine;
                                    break;
                            }
                        }
                    }
                }

                // Pr�fe ob die Klasse regelkonform ist
                player.Static = staticVariables;
                player.RequestReferences = foreignReferences;
                if (foreignReferences)
                {
                    player.RequestInformation += Resource.SimulationCoreAnalysisForeignRefInfotext +
                                                 Environment.NewLine + referenceInformation + Environment.NewLine +
                                                 Environment.NewLine;
                }

                switch (playerDefinitions)
                {
                    case 0:
                        // Kein Spielerattribut gefunden
                        throw new RuleViolationException(
                            string.Format(
                                Resource.SimulationCoreAnalysisNoPlayerAttribute,
                                player.ClassName));
                    case 1:
                        if (checkRules)
                        {
                            player.RuleCheck();
                        }
                        players.Add(player);
                        break;
                    default:
                        throw new RuleViolationException(
                            string.Format(
                                Resource.SimulationCoreAnalysisTooManyPlayerAttributes,
                                player.ClassName));
                }
            }

            #region Erkennung �lterer KI-Versionen

            // �ltere Versionen
            else if (type.BaseType.Name == "AntMe.Ameise")
            {
                // Leerer Spieler-Rumpf
                int playerDefinitions = 0;

                PlayerInfo player = new PlayerInfo();
                player.SimulationVersion = PlayerSimulationVersions.Version_1_1;
                player.ClassName = type.FullName;

                // Veraltete Version (1.1 oder 1.5)
                // TODO: Pr�fen
                if (type.GetMember("RiechtFreund") != null)
                {
                    player.SimulationVersion = PlayerSimulationVersions.Version_1_5;
                }

                // Attribute auslesen
                foreach (CustomAttributeData attribute in CustomAttributeData.GetCustomAttributes(type))
                {
                    // Spieler-Attribut auslesen
                    if (attribute.Constructor.ReflectedType.FullName == "AntMe.SpielerAttribute")
                    {
                        playerDefinitions++;
                        foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                        {
                            switch (argument.MemberInfo.Name)
                            {
                                case "Name":
                                    player.ColonyName = (string)argument.TypedValue.Value;
                                    break;
                                case "Vorname":
                                    player.FirstName = (string)argument.TypedValue.Value;
                                    break;
                                case "Nachname":
                                    player.LastName = (string)argument.TypedValue.Value;
                                    break;
                            }
                        }
                        break;
                    }

                    // Typ-Attribut auslesen
                    if (attribute.Constructor.ReflectedType.FullName == "AntMe.TypAttribute")
                    {
                        CasteInfo caste = new CasteInfo();
                        foreach (CustomAttributeNamedArgument argument in attribute.NamedArguments)
                        {
                            switch (argument.MemberInfo.Name)
                            {
                                case "Name":
                                    caste.Name = (string)argument.TypedValue.Value;
                                    break;
                                case "GeschwindigkeitModifikator":
                                    caste.Speed = (int)argument.TypedValue.Value;
                                    break;
                                case "DrehgeschwindigkeitModifikator":
                                    caste.RotationSpeed = (int)argument.TypedValue.Value;
                                    break;
                                case "LastModifikator":
                                    caste.Load = (int)argument.TypedValue.Value;
                                    break;
                                case "ReichweiteModifikator":
                                    caste.Range = (int)argument.TypedValue.Value;
                                    break;
                                case "SichtweiteModifikator":
                                    caste.ViewRange = (int)argument.TypedValue.Value;
                                    break;
                                case "EnergieModifikator":
                                    caste.Energy = (int)argument.TypedValue.Value;
                                    break;
                                case "AngriffModifikator":
                                    caste.Attack = (int)argument.TypedValue.Value;
                                    break;
                            }
                        }

                        // Bei Individualtypen zur Liste 
                        if (!caste.IsEmpty())
                        {
                            player.Castes.Add(caste);
                        }
                        break;
                    }
                }

                // Pr�fe ob die Klasse regelkonform ist
                player.Static = staticVariables;
                switch (playerDefinitions)
                {
                    case 0:
                        // Kein Spielerattribut gefunden
                        throw new RuleViolationException(
                            string.Format(
                                Resource.SimulationCoreAnalysisNoPlayerAttribute,
                                player.ClassName));
                    case 1:
                        if (checkRules)
                        {
                            player.RuleCheck();
                        }
                        players.Add(player);
                        break;
                    default:
                        throw new RuleViolationException(
                            string.Format(
                                Resource.SimulationCoreAnalysisTooManyPlayerAttributes,
                                player.ClassName));
                }
            }

            #endregion
        }

        return players;
    }

    /// <summary>
    /// List of thrown Exceptions.s
    /// </summary>
    public Exception getException()
    {
        return exception;
    }
}
