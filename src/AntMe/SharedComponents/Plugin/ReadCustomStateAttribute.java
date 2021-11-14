package AntMe.SharedComponents.Plugin;

import java.text.AttributedCharacterIterator.Attribute;
    /// <summary>
    /// Attribute, to signal, that the marked plugin reads the custom field.
    /// </summary>
//    [AttributeUsage(AttributeTargets.Class, AllowMultiple = true)]
public class ReadCustomStateAttribute {
        /// <summary>
        /// The name of the custom field.
        /// </summary>
        public String Name = "";

        /// <summary>
        /// The full name of the used type.
        /// </summary>
        public String Type = "";

        /// <summary>
        /// Optional description of usage.
        /// </summary>
        public String Description = "";

}