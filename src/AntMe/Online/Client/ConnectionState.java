package AntMe.Online.Client;

public enum ConnectionState {
        /// <summary>
        /// Connection noch nicht aufgebaut
        /// </summary>
        Disconnected,

        /// <summary>
        /// Verbindungsversuch fehlgeschlagen
        /// </summary>
        NoConnection,

        /// <summary>
        /// Vorhandener Token ist abgelaufen
        /// </summary>
        TokenInvalid,

        /// <summary>
        /// User gibt gerade Zugangsdaten ein
        /// </summary>
        LoggingIn,

        /// <summary>
        /// Verbindung aufgebaut
        /// </summary>
        Connected,
}
