/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ril.pdf_box.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ril.pdf_box.pdfbox.cos.COSArray;
import com.ril.pdf_box.pdfbox.cos.COSBase;
import com.ril.pdf_box.pdfbox.cos.COSDictionary;
import com.ril.pdf_box.pdfbox.cos.COSName;
import com.ril.pdf_box.pdfbox.cos.COSString;
import com.ril.pdf_box.pdfbox.pdmodel.common.COSArrayList;
import com.ril.pdf_box.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import com.ril.pdf_box.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import com.ril.pdf_box.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;

/**
 * A button field represents an interactive control on the screen
 * that the user can manipulate with the mouse.
 *
 * @author sug
 */
public abstract class PDButton extends PDTerminalField
{
    /**
     * A Ff flag. If set, the field is a set of radio buttons
     */
    static final int FLAG_RADIO = 1 << 15;

    /**
     * A Ff flag. If set, the field is a pushbutton.
     */
    static final int FLAG_PUSHBUTTON = 1 << 16;

    /**
     * A Ff flag. If set, radio buttons individual fields, using the same
     * value for the on state will turn on and off in unison.
     */
    static final int FLAG_RADIOS_IN_UNISON = 1 << 25;

    /**
     * @see PDField#PDField(PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDButton(PDAcroForm acroForm)
    {
        super(acroForm);
        getCOSObject().setItem(COSName.FT, COSName.BTN);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * Determines if push button bit is set.
     *
     * @return true if type of button field is a push button.
     */
    public boolean isPushButton()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_PUSHBUTTON);
    }

    /**
     * Set the push button bit.
     *
     * @param pushbutton if true the button field is treated as a push button field.
     */
    public void setPushButton(boolean pushbutton)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_PUSHBUTTON, pushbutton);
    }

    /**
     * Determines if radio button bit is set.
     *
     * @return true if type of button field is a push button.
     */
    public boolean isRadioButton()
    {
        return getCOSObject().getFlag(COSName.FF, FLAG_RADIO);
    }

    /**
     * Set the radio button bit.
     *
     * @param radiobutton if true the button field is treated as a radio button field.
     */
    public void setRadioButton(boolean radiobutton)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_RADIO, radiobutton);
    }

    /**
     * Returns the selected value. May be empty if NoToggleToOff is set but there is no value
     * selected.
     *
     * @return A non-null string.
     */
    public String getValue()
    {
        COSBase value = getInheritableAttribute(COSName.V);
        if (value instanceof COSName)
        {
            return ((COSName)value).getName();
        }
        else
        {
            return "";
        }
    }

    /**
     * Sets the selected option given its name.
     *
     * @param value Name of option to select
     *
     * @throws IOException if the value could not be set
     * @throws IllegalArgumentException if the value is not a valid option.
     */
    @Override
    public void setValue(String value) throws IOException
    {
        checkValue(value);
        getCOSObject().setName(COSName.V, value);
        // update the appearance state (AS)
        for (PDAnnotationWidget widget : getWidgets())
        {
            PDAppearanceEntry appearanceEntry = widget.getAppearance().getNormalAppearance();
            if (((COSDictionary)appearanceEntry.getCOSObject()).containsKey(value))
            {
                widget.getCOSObject().setName(COSName.AS, value);
            }
            else
            {
                widget.getCOSObject().setItem(COSName.AS, COSName.Off);
            }
        }
        applyChange();
    }


    /**
     * Returns the default value, if any.
     *
     * @return A non-null string.
     */
    public String getDefaultValue()
    {
        COSBase value = getInheritableAttribute(COSName.DV);
        if (value instanceof COSName)
        {
            return ((COSName)value).getName();
        }
        else
        {
            return "";
        }
    }

    /**
     * Sets the default value.
     *
     * @param value Name of option to select
     *
     * @throws IllegalArgumentException if the value is not a valid option.
     */
    public void setDefaultValue(String value)
    {
        checkValue(value);
        getCOSObject().setName(COSName.DV, value);
    }

    @Override
    public String getValueAsString()
    {
        return getValue();
    }

    /**
     * This will get the export values.
     *
     * <p>The export values are defined in the field dictionaries /Opt key.</p>
     *
     * <p>The option values are used to define the export values
     * for the field to
     * <ul>
     * <li>hold values in non-Latin writing systems as name objects, which represent the field value, are limited
     * to PDFDocEncoding
     * </li>
     * <li>allow radio buttons having the same export value to be handled independently
     * </li>
     * </ul>
     * </p>
     *
     * @return List containing all possible export values. If there is no Opt entry an empty list will be returned.
     */
    public List<String> getExportValues()
    {
        COSBase value = getInheritableAttribute(COSName.OPT);
        if (value instanceof COSString)
        {
            List<String> array = new ArrayList<String>();
            array.add(((COSString) value).getString());
            return array;
        }
        else if (value instanceof COSArray)
        {
            return COSArrayList.convertCOSStringCOSArrayToList((COSArray) value);
        }
        return Collections.emptyList();
    }

    /**
     * This will set the export values.
     *
     * @param values List containing all possible export values. Supplying null or an empty list will remove the Opt entry.
     * @see #getExportValues()
     */
    public void setExportValues(List<String> values)
    {
        COSArray cosValues;
        if (values != null && !values.isEmpty())
        {
            cosValues = COSArrayList.convertStringListToCOSStringCOSArray(values);
            getCOSObject().setItem(COSName.OPT, cosValues);
        }
        else
        {
            getCOSObject().removeItem(COSName.OPT);
        }
    }

    @Override
    void constructAppearances() throws IOException
    {
        for (PDAnnotationWidget widget : getWidgets())
        {
            PDAppearanceDictionary appearance = widget.getAppearance();
            if (appearance == null || appearance.getNormalAppearance() == null)
            {
                // TODO: implement appearance generation for radio buttons
                throw new UnsupportedOperationException(
                    "Appearance generation is not implemented yet, see PDFBOX-2849");
            }
            else
            {
                PDAppearanceEntry appearanceEntry = widget.getAppearance().getNormalAppearance();
                String value = getValue();
                if (((COSDictionary)appearanceEntry.getCOSObject()).containsKey(value))
                {
                    widget.getCOSObject().setName(COSName.AS, value);
                }
                else
                {
                    widget.getCOSObject().setItem(COSName.AS, COSName.Off);
                }
            }
        }
    }

    /**
     * Get the values to set individual buttons within a group to the on state.
     *
     * <p>The On value could be an arbitrary string as long as it is within the limitations of
     * a PDF name object. The Off value shall always be 'Off'. If not set or not part of the normal
     * appearance keys 'Off' is the default</p>
     *
     * @return the potential values setting the check box to the On state.
     * If an empty Set is returned there is no appearance definition.
     */
    public Set<String> getOnValues()
    {
        // we need a set as the field can appear multiple times
        Set<String> onValues = new HashSet<String>();

        List<PDAnnotationWidget> widgets = this.getWidgets();
        for (PDAnnotationWidget widget : widgets)
        {
            PDAppearanceDictionary apDictionary = widget.getAppearance();
            if (apDictionary != null)
            {
                PDAppearanceEntry normalAppearance = apDictionary.getNormalAppearance();
                if (normalAppearance != null)
                {
                    Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
                    for (COSName entry : entries)
                    {
                        if (COSName.Off.compareTo(entry) != 0)
                        {
                            onValues.add(entry.getName());
                        }
                    }
                }
            }
        }
        return onValues;
    }

    /**
     * Checks value.
     *
     * @param value Name of radio button to select
     *
     * @throws IllegalArgumentException if the value is not a valid option.
     */
    void checkValue(String value) throws IllegalArgumentException
    {
        Set<String> onValues = getOnValues();
        if (COSName.Off.getName().compareTo(value) != 0 && !onValues.contains(value))
        {
            throw new IllegalArgumentException(
                "value '" + value + "' is not a valid option for the field " +
                    getFullyQualifiedName() + ", valid values are: " + onValues + " and " +
                    COSName.Off.getName());
        }
    }
}
