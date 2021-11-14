package AntMe.Gui;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

import org.controlsfx.control.StatusBar;

import AntMe.Gui.Main;
import AntMe.Online.Client.ConnectionState;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.web.WebView;

public class AntMeController {
	
	private PluginManager manger;
	
	private PluginItem activeProducer;
	private ArrayList<PluginItem> activeConsumers = new ArrayList<PluginItem>();
	private boolean ignoreTimerEvents = false;
    private boolean initPhase = false;
    private boolean restart = false;
    private boolean directstart = false;
    private String updateUrl = "";

    private MenuBar menuStrip;
    private StatusBar statusStrip;
    private ToolBar toolStrip;
    private Menu programMenuItem;
    private Menu helpMenuItem;
    private Button startToolItem;
    private Button stopToolItem;
    private Button pauseToolItem;
    private TabPane tabControl;
    private Tab welcomeTab;
    private MenuItem startMenuItem;
    private MenuItem stopMenuItem;
    private MenuItem pauseMenuItem;
    private Separator group1MenuItem;
    private MenuItem closeMenuItem;
    private MenuItem offlineHelpMenuItem;
    private MenuItem websiteMenuItem;
    private Separator group2MenuItem;
    private MenuItem infoBoxMenuItem;
    private WebView infoWebBrowser;
    private Label sourceLabelToolItem;
    private Separator group1ToolItem;
    private Timer timer;
    private Label stateLabelBarItem;
    private ProgressBar progressBarItem;
    private Label stepCounterBarItem;
    private Label fpsBarItem;
    private Separator group2ToolItem;
    private MenuItem speedMenuItem;
    private MenuButton speedDropDownToolItem;
    private MenuItem speedMaxToolItem;
    private MenuItem speed100fpmToolItem;
    private MenuItem speed15fpmToolItem;
    private Button slowerToolItem;
    private Button fasterToolItem;
    private MenuItem speedMaxMenuItem;
    private MenuItem speed100fpmMenuItem;
    private MenuItem speed80fpmMenuItem;
    private MenuItem speed50fpmMenuItem;
    private MenuItem speed80fpmToolItem;
    private MenuItem speed50fpmToolItem;
    private MenuItem speed30fpmToolItem;
    private MenuItem speed22fpmToolItem;
    private MenuItem speed8fpmToolItem;
    private MenuItem speed2fpmToolItem;
    private Label speedLabelToolItem;
    private MenuItem speed30fpmMenuItem;
    private MenuItem speed22fpmMenuItem;
    private MenuItem speed15fpmMenuItem;
    private MenuItem speed8fpmMenuItem;
    private MenuItem speed2fpmMenuItem;
    private Menu settingsMenuItem;
    private MenuItem pluginSettingsMenuItem;
    private MenuItem languageMenuItem;
    private CheckMenuItem germanMenuItem;
    private CheckMenuItem englishMenuItem;
    private MenuItem updateMenuItem;
    private MenuButton onlineButton;
    private MenuItem profileButton;
    private MenuItem logoutButton;
    private Button loginButton;
    private Button versionButton;
    private MenuButton producerButton;
    private MenuItem wikiMenuItem;

    public AntMeController(String[] parameter)
    {
        initPhase = true;

        initializeComponent();
        onlineButton.Tag = ConnectionState.Connected;
        createHandle();

        // check Language-buttons
        if (Locale.getDefault() == Locale.GERMANY)
        {
            germanMenuItem.setSelected(true);
        }
        else
        {
            englishMenuItem.setSelected(true);
        }

        // Load Player
        Task<String> t = new Task<String>() {
            @Override protected String call() throws Exception {
                return getPlayerStore().getInstance().toString();
            }};
        t.Start();

        // Pr�fe auf Updates
        t = new Task(backgroundUpdateCheck);
        t.start();

        // Load welcomepage
        try
        {
            infoWebBrowser.Navigate(Resource.MainWelcomePageUrl);
        }
        catch { }

        manager = new PluginManager();

        try
        {
            manager.LoadSettings();
        }
        catch (Exception ex)
        {
            ExceptionViewer problems = new ExceptionViewer(ex);
            problems.ShowDialog(this);
        }

        // Set Window-Position
        WindowState = Settings.Default.windowState;
        Location = Settings.Default.windowPosition;
        Size = Settings.Default.windowSize;

        manager.SearchForPlugins();
        timer.Enabled = true;

        // Forward startparameter
        for (PluginItem plugin : manager.ProducerPlugins)
        {
            plugin.Producer.StartupParameter(parameter);
        }
        for (PluginItem plugin : manager.ConsumerPlugins)
        {
            plugin.Consumer.StartupParameter(parameter);
        }

        for (String p : parameter)
        {
            if (p.toUpperCase() == "/START")
            {
                directstart = true;
            }
        }

        initPhase = false;
    }

    /// <summary>
    /// Make updates based on manager-settings
    /// </summary>
    private void updatePanel()
    {

        if (ignoreTimerEvents)
        {
            return;
        }

        ignoreTimerEvents = true;

        // Controlling-Buttons
        startMenuItem.setDisable(!manager.canStart);
        startToolItem.setDisable(!manager.CanStart);
        pauseToolItem.setDisable(!manager.CanPause);
        pauseMenuItem.setDisable(!manager.CanPause);
        stopToolItem.setDisable(!manager.CanStop);
        stopMenuItem.setDisable(!manager.CanStop);

        if (manager.FrameLimiterEnabled)
        {
            if ((int)Math.Round(manager.FrameLimit) <= 100)
            {
                fasterToolItem.Enabled = true;
            }
            else
            {
                fasterToolItem.Enabled = false;
            }
            if ((int)Math.Round(manager.FrameLimit) > 1)
            {
                slowerToolItem.Enabled = true;
            }
            else
            {
                slowerToolItem.Enabled = false;
            }

            speedDropDownToolItem.Text = string.Format(Resource.MainFramesPerSecond, manager.FrameLimit);
        }
        else
        {
            slowerToolItem.Enabled = false;
            fasterToolItem.Enabled = false;
            speedDropDownToolItem.Text = Resource.MainSpeedMaximal;
        }

        // Producer List (Button-Based)
        List<ToolStripItem> remove = new List<ToolStripItem>();
        for (ToolStripItem item : producerButton.DropDownItems)
        {
            if (!manager.ProducerPlugins.Any(p => p == item.Tag))
                remove.Add(item);
        }
        for (var item : remove)
        {
            producerButton.DropDownItems.Remove(item);
        }

        for (var item : manager.ProducerPlugins)
        {
            if (producerButton.DropDownItems.Find(item.Guid.ToString(), false).Count() == 0)
            {
                var menuItem = new ToolStripMenuItem()
                {
                    Text = item.Name,
                    Name = item.Guid.ToString(),
                    Tag = item
                };

                menuItem.Click += button_producer;

                producerButton.DropDownItems.Add(menuItem);
            }
        }

        // manage tabs
        if (activeProducer != manager.ActiveProducerPlugin)
        {
            bool isSelected = tabControl.SelectedIndex == 1;

            // Update Mode Display
            producerButton.Text = (manager.ActiveProducerPlugin == null ? Resource.MainNoModeSelected : manager.ActiveProducerPlugin.Name);

            // remove old tab
            if (activeProducer != null)
            {
                if (activeProducer.Producer.Control != null)
                {
                    tabControl.TabPages.RemoveAt(1);
                }
                activeProducer = null;
            }

            // add new tab
            if (manager.ActiveProducerPlugin != null)
            {
                if (manager.ActiveProducerPlugin.Producer.Control != null)
                {
                    TabPage page = new TabPage(manager.ActiveProducerPlugin.Name);
                    page.Padding = new Padding(0);
                    page.Margin = new Padding(0);
                    page.Controls.Add(manager.ActiveProducerPlugin.Producer.Control);
                    manager.ActiveProducerPlugin.Producer.Control.Padding = new Padding(0);
                    manager.ActiveProducerPlugin.Producer.Control.Margin = new Padding(0);
                    tabControl.TabPages.Insert(1, page);
                    manager.ActiveProducerPlugin.Producer.Control.Dock = DockStyle.Fill;
                    if (isSelected) tabControl.SelectedIndex = 1;
                }
                activeProducer = manager.ActiveProducerPlugin;
            }
        }

        // synchronize Consumer
        List<PluginItem> newActiveConsumers = new List<PluginItem>(manager.ActiveConsumerPlugins);
        for (int i = activeConsumers.Count - 1; i >= 0; i--)
        {
            // Kick the old tab
            if (!newActiveConsumers.Contains(activeConsumers[i]))
            {
                if (tabControl.TabPages.ContainsKey(activeConsumers[i].Guid.ToString()))
                {
                    tabControl.TabPages.RemoveByKey(activeConsumers[i].Guid.ToString());
                }
                activeConsumers.Remove(activeConsumers[i]);
            }
        }
        for (PluginItem plugin : newActiveConsumers)
        {
            //Create new, if needed
            if (!activeConsumers.Contains(plugin))
            {
                // Create Tab and place control
                if (plugin.Consumer.Control != null)
                {
                    tabControl.TabPages.Add(plugin.Guid.ToString(), plugin.Name);
                    tabControl.TabPages[plugin.Guid.ToString()].Controls.Add(plugin.Consumer.Control);
                    plugin.Consumer.Control.Dock = DockStyle.Fill;
                }
                activeConsumers.Add(plugin);
            }
        }

        // popup exceptions
        if (manager.Exceptions.Count > 0)
        {
            ExceptionViewer problems = new ExceptionViewer(manager.Exceptions);
            problems.ShowDialog(this);
            manager.Exceptions.Clear();
        }

        // StatusBar-information
        stateLabelBarItem.Text = string.Empty;
        switch (manager.State)
        {
            case PluginState.NotReady:
                stateLabelBarItem.Text = Resource.MainStateNotReady;
                break;
            case PluginState.Paused:
                stateLabelBarItem.Text = Resource.MainStatePaused;
                break;
            case PluginState.Ready:
                stateLabelBarItem.Text = Resource.MainStateReady;
                break;
            case PluginState.Running:
                stateLabelBarItem.Text = Resource.MainStateRunning;
                break;
        }

        if (manager.State == PluginState.Running || manager.State == PluginState.Paused)
        {
            progressBarItem.Maximum = manager.TotalRounds;
            progressBarItem.Value = manager.CurrentRound;
            stepCounterBarItem.Text = string.Format(Resource.MainStateRoundIndicator, manager.CurrentRound, manager.TotalRounds);
            progressBarItem.Visible = true;
            stepCounterBarItem.Visible = true;
        }
        else
        {
            progressBarItem.Visible = false;
            stepCounterBarItem.Visible = false;
        }

        if (manager.State == PluginState.Running)
        {
            fpsBarItem.Text = manager.FrameRate.ToString(Resource.MainStateFramesPerSecond);
            fpsBarItem.Visible = true;
        }
        else
        {
            fpsBarItem.Visible = false;
        }

        // Online Connector
        onlineButton.Text = Connection.Instance.Username;
        onlineButton.Visible = Connection.Instance.IsLoggedIn;
        onlineButton.Enabled = !Connection.Instance.IsBusy;
        if ((ConnectionState)onlineButton.Tag != Connection.Instance.State)
        {
            switch (Connection.Instance.State)
            {
                case ConnectionState.NoConnection:
                    onlineButton .Image = Resources.connection;
                    onlineButton.ToolTipText = Resource.UpdateNoConnection;
                    break;
                case ConnectionState.TokenInvalid:
                    onlineButton.Image = Resources.warning;
                    onlineButton.ToolTipText = Resource.UpdateTokenInvalid;
                    break;
                default:
                    onlineButton.Image = Resources.online;
                    onlineButton.ToolTipText = string.Empty;
                    break;
            }
            onlineButton.Tag = Connection.Instance.State;
        }

        loginButton.Visible = !Connection.Instance.IsLoggedIn;
        loginButton.Enabled = !Connection.Instance.IsBusy;

        versionButton.Visible = !string.IsNullOrEmpty(Properties.Settings.Default.updateLink);

        ignoreTimerEvents = false;
    }

    private void form_shown(Object sender, EventArgs e)
    {
        updatePanel();

        if (manager.Exceptions.Count > 0)
        {
            ExceptionViewer problems = new ExceptionViewer(manager.Exceptions);
            problems.ShowDialog(this);
            manager.Exceptions.Clear();
        }

        // force a direkt start, if manager is ready
        if (manager.CanStart && directstart)
        {
            start(sender, e);
        }
    }

    private void form_close(Object sender, FormClosingEventArgs e)
    {
        if (manager.CanStop)
        {
            manager.Stop();
        }

        // Alle Plugin-Einstellungen absichern
        Settings.Default.Save();
        manager.SaveSettings();

        // show possible problems
        if (manager.Exceptions != null && manager.Exceptions.Count > 0)
        {
            ExceptionViewer form = new ExceptionViewer(manager.Exceptions);
            manager.Exceptions.Clear();
            form.ShowDialog(this);
        }
    }

    private void form_resize(Object sender, EventArgs e)
    {
        if (initPhase)
        {
            return;
        }

        if (WindowState == FormWindowState.Normal)
        {
            Settings.Default.windowPosition = Location;
            Settings.Default.windowSize = Size;
        }

        if (WindowState != FormWindowState.Minimized)
        {
            Settings.Default.windowState = WindowState;
        }
    }

    private void tab_select(Object sender, TabControlCancelEventArgs e)
    {
        if (e.TabPage.Tag != null)
        {
            manager.SetVisiblePlugin(((PluginItem)e.TabPage.Tag).Guid);
        }
        else
        {
            manager.SetVisiblePlugin(new Guid());
        }
    }

    private void button_close(Object sender, EventArgs e)
    {
        Close();
    }

    private void button_plugins(Object sender, EventArgs e)
    {
        ignoreTimerEvents = true;
        Plugins pluginForm = new Plugins(manager);
        pluginForm.ShowDialog(this);
        manager.SaveSettings();
        ignoreTimerEvents = false;
        updatePanel();
    }

    private void button_offlineHelp(Object sender, EventArgs e)
    {
        // Es wurde Hilfe angefordert. Hier wird gepr�ft ob eine Hilfe verf�gbar ist
        if (File.Exists(Resource.MainTutorialPath))
        {
            Help.ShowHelp(this, Resource.MainTutorialPath);
        }
        else
        {
            MessageBox.Show(
                this,
                Resource.MainMessageBoxNoHelpMessage,
                Resource.MainMessageBoxNoHelpTitle,
                MessageBoxButtons.OK,
                MessageBoxIcon.Error,
                MessageBoxDefaultButton.Button1);
        }
    }

    private void button_website(Object sender, EventArgs e)
    {
        Help.ShowHelp(this, Resource.MainWebsiteLink);
    }

    private void button_wiki(Object sender, EventArgs e)
    {
        Help.ShowHelp(this, Resource.MainWikiLink);
    }

    private void button_info(Object sender, EventArgs e)
    {
        InfoBox infoBox = new InfoBox();
        infoBox.ShowDialog(this);
    }

    private void button_limitSetTo2(Object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 2.0f);
    }

    private void button_limitSetTo8(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 8.0f);
    }

    private void button_limitSetTo15(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 15.0f);
    }

    private void button_limitSetTo22(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 22.5f);
    }

    private void button_limitSetTo30(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 30.0f);
    }

    private void button_limitSetTo50(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 50.0f);
    }

    private void button_limitSetTo80(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 80.0f);
    }

    private void button_limitSetTo100(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(true, 100.0f);
    }

    private void button_limitSetToMax(object sender, EventArgs e)
    {
        manager.SetSpeedLimit(false, 0.0f);
    }

    private void button_limitFaster(object sender, EventArgs e)
    {
        if (manager.FrameLimiterEnabled)
        {
            if ((int)Math.Round(manager.FrameLimit) < 100)
            {
                manager.SetSpeedLimit(true, (int)Math.Round(manager.FrameLimit) + 1);
            }
            else
            {
                manager.SetSpeedLimit(false, 0.0f);
            }
        }
    }

    private void button_limitSlower(object sender, EventArgs e)
    {
        if (manager.FrameLimiterEnabled && (int)Math.Round(manager.FrameLimit) > 1)
        {
            manager.SetSpeedLimit(true, (int)Math.Round(manager.FrameLimit) - 1);
        }
    }

    private void button_german(object sender, EventArgs e)
    {
        Settings.Default.culture = "de";
        Settings.Default.Save();
        restart = true;
        Close();
    }

    private void button_english(object sender, EventArgs e)
    {
        Settings.Default.culture = "en";
        Settings.Default.Save();
        restart = true;
        Close();
    }

    private void button_switchAutoupdate(object sender, EventArgs e)
    {
        try
        {
            Uri download = Connection.Instance.CheckForUpdates(
                Assembly.GetExecutingAssembly().GetName().Version);
            
            if (download != null)
            {
                if (MessageBox.Show(this, Resource.UpdateNewerMessage, 
                    Resource.UpdateTitle, MessageBoxButtons.YesNo, MessageBoxIcon.Information) == System.Windows.Forms.DialogResult.Yes)
                {
                    Process.Start(download.ToString());
                }
            }
            else
            {
                MessageBox.Show(this, Resource.UpdateNewestMessage, Resource.UpdateTitle, 
                    MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show(this, Resource.UpdateErrorMessage, Resource.UpdateTitle, 
                MessageBoxButtons.OK, MessageBoxIcon.Warning);
        }
    }

    private void button_producer(object sender, EventArgs e)
    {
        if (ignoreTimerEvents)
            return;

        ignoreTimerEvents = true;

        ToolStripMenuItem menuItem = (ToolStripMenuItem) sender;
        PluginItem plugin = (PluginItem) menuItem.Tag;
        manager.ActivateProducer(plugin.Guid);

        updatePanel();
        ignoreTimerEvents = false;
    }

    private void timer_tick(object sender, EventArgs e)
    {
        if (!ignoreTimerEvents)
        {
            updatePanel();
        }
    }

    private void start(object sender, EventArgs e)
    {
        if (manager.CanStart)
        {
            manager.Start();

            // Aktives Eingangsplugin anzeigen
            if (activeProducer.Producer.Control != null)
            {
                tabControl.SelectedIndex = 1;
            }
        }
    }

    private void stop(object sender, EventArgs e)
    {
        if (manager.CanStop)
        {
            manager.Stop();
        }
    }

    private void pause(object sender, EventArgs e)
    {
        if (manager.CanPause)
        {
            manager.Pause();
        }
    }

    public boolean getRestart
    {
        return restart;
    }


    private void logoutButton_Click(object sender, EventArgs e)
    {
        if (Connection.Instance.IsLoggedIn)
            Connection.Instance.Close();
    }

    private void profileButton_Click(object sender, EventArgs e)
    {
        Process.Start("https://service.antme.net/Account");
    }

    private void loginButton_Click(object sender, EventArgs e)
    {
        if (!Connection.Instance.IsLoggedIn)
            Connection.Instance.Open(this);
    }

    private void BackgroundUpdateCheck()
    {
        // Check every day
        if (Settings.Default.lastUpdateCheck < DateTime.Now.Date)
        {
            try
            {
                Uri download = Connection.Instance.CheckForUpdates(
                    Assembly.GetExecutingAssembly().GetName().Version);

                if (download != null)
                    Settings.Default.updateLink = download.ToString();
                else
                    Settings.Default.updateLink = string.Empty;
            }
            catch (Exception) {}

            Settings.Default.lastUpdateCheck = DateTime.Now.Date;
            Settings.Default.Save();
        }
    }

    private void infoWebBrowser_NavigateError(object sender, WebBrowserNavigateErrorEventArgs e)
    {
        infoWebBrowser.Navigate("file://" + Application.StartupPath + Resource.MainWelcomePagePath);
    }

    private void versionButton_Click(object sender, EventArgs e)
    {
        if (MessageBox.Show(this, Resource.UpdateNewerMessage, Resource.UpdateTitle, 
            MessageBoxButtons.YesNo, MessageBoxIcon.Information) == System.Windows.Forms.DialogResult.Yes)
        {
            // Open Link
            Process.Start(Settings.Default.updateLink);

            // Clear Update Link
            Settings.Default.updateLink = string.Empty;
            Settings.Default.Save();
        }
    }
    /// <summary>
    /// Required designer variable.
    /// </summary>
    //private System.ComponentModel.IContainer components = null;

    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
    @Override
    protected void dispose(boolean disposing)
    {
        if (disposing && (components != null))
        {
            components.dispose();
        }
        base.dispose(disposing);
    }

 
    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void initializeComponent()
    {
 //       this.components = new System.ComponentModel.Container();
//        System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Main));
        this.menuStrip = new MenuBar();
        this.programMenuItem = new Menu();
        this.startMenuItem = new MenuItem();
        this.stopMenuItem = new MenuItem();
        this.pauseMenuItem = new MenuItem();
        this.group1MenuItem = new Separator();
        this.closeMenuItem = new MenuItem();
        this.settingsMenuItem = new Menu();
        this.pluginSettingsMenuItem = new MenuItem();
        this.speedMenuItem = new MenuItem();
        this.speedMaxMenuItem = new MenuItem();
        this.speed100fpmMenuItem = new MenuItem();
        this.speed80fpmMenuItem = new MenuItem();
        this.speed50fpmMenuItem = new MenuItem();
        this.speed30fpmMenuItem = new MenuItem();
        this.speed22fpmMenuItem = new MenuItem();
        this.speed15fpmMenuItem = new MenuItem();
        this.speed8fpmMenuItem = new MenuItem();
        this.speed2fpmMenuItem = new MenuItem();
        this.languageMenuItem = new MenuItem();
        this.germanMenuItem = new CheckMenuItem();
        this.englishMenuItem = new CheckMenuItem();
        this.updateMenuItem = new MenuItem();
        this.helpMenuItem = new Menu();
        this.offlineHelpMenuItem = new MenuItem();
        this.websiteMenuItem = new MenuItem();
        this.wikiMenuItem = new MenuItem();
        this.group2MenuItem = new Separator();
        this.infoBoxMenuItem = new MenuItem();
        this.statusStrip = new StatusBar();
        this.stateLabelBarItem = new Label();
        this.progressBarItem = new ProgressBar();
        this.stepCounterBarItem = new Label();
        this.fpsBarItem = new Label();
        this.toolStrip = new ToolBar();
        this.startToolItem = new Button();
        this.stopToolItem = new Button();
        this.pauseToolItem = new Button();
        this.group1ToolItem = new Separator();
        this.sourceLabelToolItem = new Label();
        this.producerButton = new MenuButton();
        this.group2ToolItem = new Separator();
        this.speedLabelToolItem = new Label();
        this.speedDropDownToolItem = new MenuButton();
        this.speedMaxToolItem = new MenuItem();
        this.speed100fpmToolItem = new MenuItem();
        this.speed80fpmToolItem = new MenuItem();
        this.speed50fpmToolItem = new MenuItem();
        this.speed30fpmToolItem = new MenuItem();
        this.speed22fpmToolItem = new MenuItem();
        this.speed15fpmToolItem = new MenuItem();
        this.speed8fpmToolItem = new MenuItem();
        this.speed2fpmToolItem = new MenuItem();
        this.slowerToolItem = new Button();
        this.fasterToolItem = new Button();
        this.onlineButton = new MenuButton();
        this.profileButton = new MenuItem();
        this.logoutButton = new MenuItem();
        this.loginButton = new Button();
        this.versionButton = new Button();
        this.tabControl = new TabPane();
        this.welcomeTab = new Tab();
        this.infoWebBrowser = new WebView();
        this.timer = new Timer("components");
        //this.menuStrip.suspendLayout();
        //this.statusStrip.suspendLayout();
        //this.toolStrip.suspendLayout();
        //this.tabControl.suspendLayout();
        //this.welcomeTab.suspendLayout();
        //this.suspendLayout();
        // 
        // menuStrip
        // 
        this.menuStrip.getMenus().addAll(this.programMenuItem,this.settingsMenuItem,this.helpMenuItem);
//        resources.ApplyResources(this.menuStrip, "menuStrip");
        this.menuStrip.setId("menuStrip");
        // 
        // programMenuItem
        // 
        this.programMenuItem.getItems().addAll(this.startMenuItem,this.stopMenuItem,this.pauseMenuItem,/*this.group1MenuItem,*/this.closeMenuItem);
        this.programMenuItem.setId("programMenuItem");
//        resources.ApplyResources(this.programMenuItem, "programMenuItem");
        // 
        // startMenuItem
        // 
//        resources.ApplyResources(this.startMenuItem, "startMenuItem");
        this.startMenuItem.Image = global::AntMe.Gui.Properties.Resources.play;
        this.startMenuItem.Name = "startMenuItem";
        this.startMenuItem.Click += new System.EventHandler(this.start);
        // 
        // stopMenuItem
        // 
        resources.ApplyResources(this.stopMenuItem, "stopMenuItem");
        this.stopMenuItem.Image = global::AntMe.Gui.Properties.Resources.stop;
        this.stopMenuItem.Name = "stopMenuItem";
        this.stopMenuItem.Click += new System.EventHandler(this.stop);
        // 
        // pauseMenuItem
        // 
        resources.ApplyResources(this.pauseMenuItem, "pauseMenuItem");
        this.pauseMenuItem.Image = global::AntMe.Gui.Properties.Resources.pause;
        this.pauseMenuItem.Name = "pauseMenuItem";
        this.pauseMenuItem.Click += new System.EventHandler(this.pause);
        // 
        // group1MenuItem
        // 
        this.group1MenuItem.Name = "group1MenuItem";
        resources.ApplyResources(this.group1MenuItem, "group1MenuItem");
        // 
        // closeMenuItem
        // 
        this.closeMenuItem.Name = "closeMenuItem";
        resources.ApplyResources(this.closeMenuItem, "closeMenuItem");
        this.closeMenuItem.Click += new System.EventHandler(this.button_close);
        // 
        // settingsMenuItem
        // 
        this.settingsMenuItem.DropDownItems.AddRange(new Item[] {
        this.pluginSettingsMenuItem,
        this.speedMenuItem,
        this.languageMenuItem,
        this.updateMenuItem});
        this.settingsMenuItem.Name = "settingsMenuItem";
        resources.ApplyResources(this.settingsMenuItem, "settingsMenuItem");
        // 
        // pluginSettingsMenuItem
        // 
        this.pluginSettingsMenuItem.Name = "pluginSettingsMenuItem";
        resources.ApplyResources(this.pluginSettingsMenuItem, "pluginSettingsMenuItem");
        this.pluginSettingsMenuItem.Click += new System.EventHandler(this.button_plugins);
        // 
        // speedMenuItem
        // 
        this.speedMenuItem.DropDownItems.AddRange(new Item[] {
        this.speedMaxMenuItem,
        this.speed100fpmMenuItem,
        this.speed80fpmMenuItem,
        this.speed50fpmMenuItem,
        this.speed30fpmMenuItem,
        this.speed22fpmMenuItem,
        this.speed15fpmMenuItem,
        this.speed8fpmMenuItem,
        this.speed2fpmMenuItem});
        this.speedMenuItem.Image = global::AntMe.Gui.Properties.Resources.speed;
        this.speedMenuItem.Name = "speedMenuItem";
        resources.ApplyResources(this.speedMenuItem, "speedMenuItem");
        // 
        // speedMaxMenuItem
        // 
        this.speedMaxMenuItem.Name = "speedMaxMenuItem";
        resources.ApplyResources(this.speedMaxMenuItem, "speedMaxMenuItem");
        this.speedMaxMenuItem.Click += new System.EventHandler(this.button_limitSetToMax);
        // 
        // speed100fpmMenuItem
        // 
        this.speed100fpmMenuItem.Name = "speed100fpmMenuItem";
        resources.ApplyResources(this.speed100fpmMenuItem, "speed100fpmMenuItem");
        this.speed100fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo100);
        // 
        // speed80fpmMenuItem
        // 
        this.speed80fpmMenuItem.Name = "speed80fpmMenuItem";
        resources.ApplyResources(this.speed80fpmMenuItem, "speed80fpmMenuItem");
        this.speed80fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo80);
        // 
        // speed50fpmMenuItem
        // 
        this.speed50fpmMenuItem.Name = "speed50fpmMenuItem";
        resources.ApplyResources(this.speed50fpmMenuItem, "speed50fpmMenuItem");
        this.speed50fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo50);
        // 
        // speed30fpmMenuItem
        // 
        this.speed30fpmMenuItem.Name = "speed30fpmMenuItem";
        resources.ApplyResources(this.speed30fpmMenuItem, "speed30fpmMenuItem");
        this.speed30fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo30);
        // 
        // speed22fpmMenuItem
        // 
        this.speed22fpmMenuItem.Name = "speed22fpmMenuItem";
        resources.ApplyResources(this.speed22fpmMenuItem, "speed22fpmMenuItem");
        this.speed22fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo22);
        // 
        // speed15fpmMenuItem
        // 
        this.speed15fpmMenuItem.Name = "speed15fpmMenuItem";
        resources.ApplyResources(this.speed15fpmMenuItem, "speed15fpmMenuItem");
        this.speed15fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo15);
        // 
        // speed8fpmMenuItem
        // 
        this.speed8fpmMenuItem.Name = "speed8fpmMenuItem";
        resources.ApplyResources(this.speed8fpmMenuItem, "speed8fpmMenuItem");
        this.speed8fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo8);
        // 
        // speed2fpmMenuItem
        // 
        this.speed2fpmMenuItem.Name = "speed2fpmMenuItem";
        resources.ApplyResources(this.speed2fpmMenuItem, "speed2fpmMenuItem");
        this.speed2fpmMenuItem.Click += new System.EventHandler(this.button_limitSetTo2);
        // 
        // languageMenuItem
        // 
        this.languageMenuItem.DropDownItems.AddRange(new Item[] {
        this.germanMenuItem,
        this.englishMenuItem});
        this.languageMenuItem.Name = "languageMenuItem";
        resources.ApplyResources(this.languageMenuItem, "languageMenuItem");
        // 
        // germanMenuItem
        // 
        this.germanMenuItem.Name = "germanMenuItem";
        resources.ApplyResources(this.germanMenuItem, "germanMenuItem");
        this.germanMenuItem.Click += new System.EventHandler(this.button_german);
        // 
        // englishMenuItem
        // 
        this.englishMenuItem.Name = "englishMenuItem";
        resources.ApplyResources(this.englishMenuItem, "englishMenuItem");
        this.englishMenuItem.Click += new System.EventHandler(this.button_english);
        // 
        // updateMenuItem
        // 
        this.updateMenuItem.Name = "updateMenuItem";
        resources.ApplyResources(this.updateMenuItem, "updateMenuItem");
        this.updateMenuItem.Click += new System.EventHandler(this.button_switchAutoupdate);
        // 
        // helpMenuItem
        // 
        this.helpMenuItem.DropDownItems.AddRange(new Item[] {
        this.offlineHelpMenuItem,
        this.websiteMenuItem,
        this.wikiMenuItem,
        this.group2MenuItem,
        this.infoBoxMenuItem});
        this.helpMenuItem.Name = "helpMenuItem";
        resources.ApplyResources(this.helpMenuItem, "helpMenuItem");
        // 
        // offlineHelpMenuItem
        // 
        this.offlineHelpMenuItem.Name = "offlineHelpMenuItem";
        resources.ApplyResources(this.offlineHelpMenuItem, "offlineHelpMenuItem");
        this.offlineHelpMenuItem.Click += new System.EventHandler(this.button_offlineHelp);
        // 
        // websiteMenuItem
        // 
        this.websiteMenuItem.Name = "websiteMenuItem";
        resources.ApplyResources(this.websiteMenuItem, "websiteMenuItem");
        this.websiteMenuItem.Click += new System.EventHandler(this.button_website);
        // 
        // wikiMenuItem
        // 
        this.wikiMenuItem.Name = "wikiMenuItem";
        resources.ApplyResources(this.wikiMenuItem, "wikiMenuItem");
        this.wikiMenuItem.Click += new System.EventHandler(this.button_wiki);
        // 
        // group2MenuItem
        // 
        this.group2MenuItem.Name = "group2MenuItem";
        resources.ApplyResources(this.group2MenuItem, "group2MenuItem");
        // 
        // infoBoxMenuItem
        // 
        this.infoBoxMenuItem.Name = "infoBoxMenuItem";
        resources.ApplyResources(this.infoBoxMenuItem, "infoBoxMenuItem");
        this.infoBoxMenuItem.Click += new System.EventHandler(this.button_info);
        // 
        // statusStrip
        // 
        this.statusStrip.Items.AddRange(new Item[] {
        this.stateLabelBarItem,
        this.progressBarItem,
        this.stepCounterBarItem,
        this.fpsBarItem});
        resources.ApplyResources(this.statusStrip, "statusStrip");
        this.statusStrip.Name = "statusStrip";
        // 
        // stateLabelBarItem
        // 
        resources.ApplyResources(this.stateLabelBarItem, "stateLabelBarItem");
        this.stateLabelBarItem.Name = "stateLabelBarItem";
        this.stateLabelBarItem.Overflow = ItemOverflow.Never;
        // 
        // progressBarItem
        // 
        this.progressBarItem.Name = "progressBarItem";
        resources.ApplyResources(this.progressBarItem, "progressBarItem");
        this.progressBarItem.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
        // 
        // stepCounterBarItem
        // 
        this.stepCounterBarItem.Name = "stepCounterBarItem";
        resources.ApplyResources(this.stepCounterBarItem, "stepCounterBarItem");
        // 
        // fpsBarItem
        // 
        this.fpsBarItem.Name = "fpsBarItem";
        resources.ApplyResources(this.fpsBarItem, "fpsBarItem");
        this.fpsBarItem.Spring = true;
        // 
        // toolStrip
        // 
        this.toolStrip.Items.AddRange(new Item[] {
        this.startToolItem,
        this.stopToolItem,
        this.pauseToolItem,
        this.group1ToolItem,
        this.sourceLabelToolItem,
        this.producerButton,
        this.group2ToolItem,
        this.speedLabelToolItem,
        this.speedDropDownToolItem,
        this.slowerToolItem,
        this.fasterToolItem,
        this.onlineButton,
        this.loginButton,
        this.versionButton});
        resources.ApplyResources(this.toolStrip, "toolStrip");
        this.toolStrip.Name = "toolStrip";
        // 
        // startToolItem
        // 
        this.startToolItem.DisplayStyle = ItemDisplayStyle.Image;
        resources.ApplyResources(this.startToolItem, "startToolItem");
        this.startToolItem.Image = global::AntMe.Gui.Properties.Resources.play;
        this.startToolItem.Name = "startToolItem";
        this.startToolItem.Click += new System.EventHandler(this.start);
        // 
        // stopToolItem
        // 
        this.stopToolItem.DisplayStyle = ItemDisplayStyle.Image;
        resources.ApplyResources(this.stopToolItem, "stopToolItem");
        this.stopToolItem.Image = global::AntMe.Gui.Properties.Resources.stop;
        this.stopToolItem.Name = "stopToolItem";
        this.stopToolItem.Click += new System.EventHandler(this.stop);
        // 
        // pauseToolItem
        // 
        this.pauseToolItem.DisplayStyle = ItemDisplayStyle.Image;
        resources.ApplyResources(this.pauseToolItem, "pauseToolItem");
        this.pauseToolItem.Image = global::AntMe.Gui.Properties.Resources.pause;
        this.pauseToolItem.Name = "pauseToolItem";
        this.pauseToolItem.Click += new System.EventHandler(this.pause);
        // 
        // group1ToolItem
        // 
        this.group1ToolItem.Name = "group1ToolItem";
        resources.ApplyResources(this.group1ToolItem, "group1ToolItem");
        // 
        // sourceLabelToolItem
        // 
        this.sourceLabelToolItem.Name = "sourceLabelToolItem";
        resources.ApplyResources(this.sourceLabelToolItem, "sourceLabelToolItem");
        // 
        // producerButton
        // 
        this.producerButton.AutoToolTip = false;
        this.producerButton.DisplayStyle = ItemDisplayStyle.Text;
        resources.ApplyResources(this.producerButton, "producerButton");
        this.producerButton.Name = "producerButton";
        // 
        // group2ToolItem
        // 
        this.group2ToolItem.Name = "group2ToolItem";
        resources.ApplyResources(this.group2ToolItem, "group2ToolItem");
        // 
        // speedLabelToolItem
        // 
        this.speedLabelToolItem.Name = "speedLabelToolItem";
        resources.ApplyResources(this.speedLabelToolItem, "speedLabelToolItem");
        // 
        // speedDropDownToolItem
        // 
        this.speedDropDownToolItem.DropDownItems.AddRange(new Item[] {
        this.speedMaxToolItem,
        this.speed100fpmToolItem,
        this.speed80fpmToolItem,
        this.speed50fpmToolItem,
        this.speed30fpmToolItem,
        this.speed22fpmToolItem,
        this.speed15fpmToolItem,
        this.speed8fpmToolItem,
        this.speed2fpmToolItem});
        this.speedDropDownToolItem.Image = global::AntMe.Gui.Properties.Resources.speed;
        resources.ApplyResources(this.speedDropDownToolItem, "speedDropDownToolItem");
        this.speedDropDownToolItem.Name = "speedDropDownToolItem";
        // 
        // speedMaxToolItem
        // 
        this.speedMaxToolItem.Name = "speedMaxToolItem";
        resources.ApplyResources(this.speedMaxToolItem, "speedMaxToolItem");
        this.speedMaxToolItem.Click += new System.EventHandler(this.button_limitSetToMax);
        // 
        // speed100fpmToolItem
        // 
        this.speed100fpmToolItem.Name = "speed100fpmToolItem";
        resources.ApplyResources(this.speed100fpmToolItem, "speed100fpmToolItem");
        this.speed100fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo100);
        // 
        // speed80fpmToolItem
        // 
        this.speed80fpmToolItem.Name = "speed80fpmToolItem";
        resources.ApplyResources(this.speed80fpmToolItem, "speed80fpmToolItem");
        this.speed80fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo80);
        // 
        // speed50fpmToolItem
        // 
        this.speed50fpmToolItem.Name = "speed50fpmToolItem";
        resources.ApplyResources(this.speed50fpmToolItem, "speed50fpmToolItem");
        this.speed50fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo50);
        // 
        // speed30fpmToolItem
        // 
        this.speed30fpmToolItem.Name = "speed30fpmToolItem";
        resources.ApplyResources(this.speed30fpmToolItem, "speed30fpmToolItem");
        this.speed30fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo30);
        // 
        // speed22fpmToolItem
        // 
        this.speed22fpmToolItem.Name = "speed22fpmToolItem";
        resources.ApplyResources(this.speed22fpmToolItem, "speed22fpmToolItem");
        this.speed22fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo22);
        // 
        // speed15fpmToolItem
        // 
        this.speed15fpmToolItem.Name = "speed15fpmToolItem";
        resources.ApplyResources(this.speed15fpmToolItem, "speed15fpmToolItem");
        this.speed15fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo15);
        // 
        // speed8fpmToolItem
        // 
        this.speed8fpmToolItem.Name = "speed8fpmToolItem";
        resources.ApplyResources(this.speed8fpmToolItem, "speed8fpmToolItem");
        this.speed8fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo8);
        // 
        // speed2fpmToolItem
        // 
        this.speed2fpmToolItem.Name = "speed2fpmToolItem";
        resources.ApplyResources(this.speed2fpmToolItem, "speed2fpmToolItem");
        this.speed2fpmToolItem.Click += new System.EventHandler(this.button_limitSetTo2);
        // 
        // slowerToolItem
        // 
        this.slowerToolItem.DisplayStyle = ItemDisplayStyle.Image;
        this.slowerToolItem.Image = global::AntMe.Gui.Properties.Resources.downarrow;
        resources.ApplyResources(this.slowerToolItem, "slowerToolItem");
        this.slowerToolItem.Name = "slowerToolItem";
        this.slowerToolItem.Click += new System.EventHandler(this.button_limitSlower);
        // 
        // fasterToolItem
        // 
        this.fasterToolItem.DisplayStyle = ItemDisplayStyle.Image;
        this.fasterToolItem.Image = global::AntMe.Gui.Properties.Resources.uparrow;
        resources.ApplyResources(this.fasterToolItem, "fasterToolItem");
        this.fasterToolItem.Name = "fasterToolItem";
        this.fasterToolItem.Click += new System.EventHandler(this.button_limitFaster);
        // 
        // onlineButton
        // 
        this.onlineButton.Alignment = ItemAlignment.Right;
        this.onlineButton.AutoToolTip = false;
        this.onlineButton.DropDownItems.AddRange(new Item[] {
        this.profileButton,
        this.logoutButton});
        this.onlineButton.Image = global::AntMe.Gui.Properties.Resources.online;
        resources.ApplyResources(this.onlineButton, "onlineButton");
        this.onlineButton.Name = "onlineButton";
        // 
        // profileButton
        // 
        this.profileButton.Name = "profileButton";
        resources.ApplyResources(this.profileButton, "profileButton");
        this.profileButton.Click += new System.EventHandler(this.profileButton_Click);
        // 
        // logoutButton
        // 
        this.logoutButton.Name = "logoutButton";
        resources.ApplyResources(this.logoutButton, "logoutButton");
        this.logoutButton.Click += new System.EventHandler(this.logoutButton_Click);
        // 
        // loginButton
        // 
        this.loginButton.Alignment = ItemAlignment.Right;
        this.loginButton.Image = global::AntMe.Gui.Properties.Resources.offline;
        resources.ApplyResources(this.loginButton, "loginButton");
        this.loginButton.Name = "loginButton";
        this.loginButton.Click += new System.EventHandler(this.loginButton_Click);
        // 
        // versionButton
        // 
        this.versionButton.Alignment = ItemAlignment.Right;
        this.versionButton.AutoToolTip = false;
        this.versionButton.Image = global::AntMe.Gui.Properties.Resources.warning;
        resources.ApplyResources(this.versionButton, "versionButton");
        this.versionButton.Name = "versionButton";
        this.versionButton.Click += new System.EventHandler(this.versionButton_Click);
        // 
        // tabControl
        // 
        this.tabControl.Controls.Add(this.welcomeTab);
        resources.ApplyResources(this.tabControl, "tabControl");
        this.tabControl.Name = "tabControl";
        this.tabControl.SelectedIndex = 0;
        this.tabControl.Selecting += new System.Windows.Forms.TabControlCancelEventHandler(this.tab_select);
        // 
        // welcomeTab
        // 
        this.welcomeTab.Controls.Add(this.infoWebBrowser);
        resources.ApplyResources(this.welcomeTab, "welcomeTab");
        this.welcomeTab.Name = "welcomeTab";
        this.welcomeTab.UseVisualStyleBackColor = true;
        // 
        // infoWebBrowser
        // 
        resources.ApplyResources(this.infoWebBrowser, "infoWebBrowser");
        this.infoWebBrowser.IsWebBrowserContextMenuEnabled = false;
        this.infoWebBrowser.Name = "infoWebBrowser";
        this.infoWebBrowser.ScriptErrorsSuppressed = true;
        this.infoWebBrowser.WebBrowserShortcutsEnabled = false;
        this.infoWebBrowser.NavigateError += new AntMe.Gui.WebBrowserNavigateErrorEventHandler(this.infoWebBrowser_NavigateError);
        // 
        // timer
        // 
        this.timer.Tick += new System.EventHandler(this.timer_tick);
        // 
        // Main
        // 
        resources.ApplyResources(this, "$this");
        this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
        this.Controls.Add(this.tabControl);
        this.Controls.Add(this.statusStrip);
        this.Controls.Add(this.toolStrip);
        this.Controls.Add(this.menuStrip);
        this.MainMenuStrip = this.menuStrip;
        this.Name = "Main";
        this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.form_close);
        this.Shown += new System.EventHandler(this.form_shown);
        this.Move += new System.EventHandler(this.form_resize);
        this.Resize += new System.EventHandler(this.form_resize);
        this.menuStrip.ResumeLayout(false);
        this.menuStrip.PerformLayout();
        this.statusStrip.ResumeLayout(false);
        this.statusStrip.PerformLayout();
        this.toolStrip.ResumeLayout(false);
        this.toolStrip.PerformLayout();
        this.tabControl.ResumeLayout(false);
        this.welcomeTab.ResumeLayout(false);
        this.ResumeLayout(false);
        this.PerformLayout();

    }

}	
}
