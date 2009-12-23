package org.trivee.fb2pdf;

import java.lang.reflect.Type;
import com.google.gson.*;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

public class ParagraphStyle
{
    private static final class FontStyleInfo
    {
        private String style;
        private boolean fontBold;
        private boolean fontItalic;

        public FontStyleInfo(String style)
            throws FB2toPDFException
        {
            this.style = style;
            if (style.equalsIgnoreCase("regular"))
            {
                fontBold = false;
                fontItalic = false;
            }
            else if (style.equalsIgnoreCase("bold"))
            {
                fontBold = true;
                fontItalic = false;
            }
            else if (style.equalsIgnoreCase("italic"))
            {
                fontBold = false;
                fontItalic = true;
            }
            else if (style.equalsIgnoreCase("bolditalic"))
            {
                fontBold = true;
                fontItalic = true;
            }
            else
            {
                throw new FB2toPDFException("Invalid style '" + style + "'");
            }
        }

        public boolean isFontBold()
        {
            return fontBold;
        }

        public boolean isFontItalic()
        {
            return fontItalic;
        }
    };

    private static final class FontStyleInfoIO
        implements JsonDeserializer<FontStyleInfo>,JsonSerializer<FontStyleInfo>
    {
        public FontStyleInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
        {
            try
            {
                return new FontStyleInfo(json.getAsString());
            }
            catch(FB2toPDFException e)
            {
                throw new JsonParseException(e);
            }
        }

        public JsonElement serialize(FontStyleInfo info, Type typeOfId, JsonSerializationContext context)
        {
            return new JsonPrimitive(info.style);
        }
    }

    private static final class AlignmentInfo
    {
        private String alignment;
        private int alignmentValue;

        public AlignmentInfo(String alignment)
            throws FB2toPDFException
        {
            this.alignment = alignment;
            if (alignment.equalsIgnoreCase("left"))
                this.alignmentValue = Paragraph.ALIGN_LEFT;
            else if (alignment.equalsIgnoreCase("center"))
                this.alignmentValue = Paragraph.ALIGN_CENTER;
            else if (alignment.equalsIgnoreCase("right"))
                this.alignmentValue = Paragraph.ALIGN_RIGHT;
            else if (alignment.equalsIgnoreCase("justified"))
                this.alignmentValue = Paragraph.ALIGN_JUSTIFIED;
            else
                throw new FB2toPDFException("Invalid alignment '" + alignment + "'");
        }

        public int getAlignmentValue()
        {
            return alignmentValue;
        }
    };

    private static final class AlignmentInfoIO
        implements JsonDeserializer<AlignmentInfo>,JsonSerializer<AlignmentInfo>
    {
        public AlignmentInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
        {
            try
            {
                return new AlignmentInfo(json.getAsString());
            }
            catch(FB2toPDFException e)
            {
                throw new JsonParseException(e);
            }
        }

        public JsonElement serialize(AlignmentInfo info, Type typeOfId, JsonSerializationContext context)
        {
            return new JsonPrimitive(info.alignment);
        }
    }

    public static GsonBuilder prepare(GsonBuilder gsonBuilder)
    {
        return gsonBuilder
            .registerTypeAdapter(FontStyleInfo.class, new FontStyleInfoIO())
            .registerTypeAdapter(AlignmentInfo.class, new AlignmentInfoIO());
    }


    private transient Stylesheet stylesheet;

    private String name;
    private String baseStyle;

    private String fontFamily;
    private FontStyleInfo fontStyle;
    private transient boolean boldToggle;
    private transient boolean italicToggle;
    private Dimension fontSize;
    private Dimension leading;
    private AlignmentInfo alignment;
    private Dimension spacingBefore;
    private Dimension firstSpacingBefore;
    private Dimension spacingAfter;
    private Dimension lastSpacingAfter;
    private Dimension leftIndent;
    private Dimension firstLineIndent;
    private Dimension firstFirstLineIndent;
    private Boolean disableHyphenation;
    private String dropcapStyle;

    private String text;
    
    public ParagraphStyle()
    {
    }

    public void setStylesheet(Stylesheet stylesheet)
    {
        this.stylesheet = stylesheet;
    }

    public Stylesheet getStylesheet()
    {
        return stylesheet;
    }

    public String getName()
    {
        return name;
    }

    public BaseFont getBaseFont() throws FB2toPDFException {
        FontFamily ff = getFontFamily();
        FontStyleInfo fs = getFontStyle();
        boolean bold = fs.isFontBold();
        if (boldToggle) {
            bold = !bold;
        }
        boolean italic = fs.isFontItalic();
        if (italicToggle) {
            italic = !italic;
        }
        BaseFont bf = italic ? (bold ? ff.getBoldItalicFont() : ff.getItalicFont()) : (bold ? ff.getBoldFont() : ff.getRegularFont());
        return bf;
    }

    private ParagraphStyle getBaseStyle()
        throws FB2toPDFException
    {
        if (baseStyle == null)
            return null;

        if (stylesheet == null)
            throw new FB2toPDFException("Stylesheet not set.");

        return stylesheet.getParagraphStyle(baseStyle);
    }
    
    public FontFamily getFontFamily()
        throws FB2toPDFException
    {
        if (stylesheet == null)
            throw new FB2toPDFException("Stylesheet not set.");

        if (fontFamily != null)
            return stylesheet.getFontFamily(fontFamily);

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFontFamily();

        throw new FB2toPDFException("Font family for style " + name + " not defined.");
    }

    private FontStyleInfo getFontStyle()
        throws FB2toPDFException
    {
        if (fontStyle != null)
            return fontStyle;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFontStyle();

        // font style defaults to regular
        return new FontStyleInfo("regular");
    }
    
    public Dimension getFontSize()
        throws FB2toPDFException
    {
        if (fontSize != null)
            return fontSize;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFontSize();

        throw new FB2toPDFException("Font size for style " + name + " not defined.");
    }

    public boolean getDisableHyphenation()
        throws FB2toPDFException
    {
        if (disableHyphenation != null)
            return disableHyphenation;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getDisableHyphenation();

        return new Boolean(false);
    }

    public String getDropcapStyle() throws FB2toPDFException
    {
        if (dropcapStyle != null)
            return dropcapStyle;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getDropcapStyle();

        return "";
    }

    public void toggleBold()
    {
        boldToggle = !boldToggle;
    }

    public void toggleItalic()
    {
        italicToggle = !italicToggle;
    }
    
    public Font getFont()
        throws FB2toPDFException
    {

        BaseFont bf = getBaseFont();

        return new Font(bf, getFontSize().getPoints());
    }

    public Font getTinyFont()
        throws FB2toPDFException
    {
        FontFamily ff = getFontFamily();
        BaseFont bf = ff.getRegularFont();

        return new Font(bf, 0.01f);
    }

    private Dimension getLeadingDimension()
        throws FB2toPDFException
    {
        if (leading != null)
            return leading;
        
        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getLeadingDimension();

        // leading defaults to 1em
        return new Dimension("1em");
    }

    public float getAbsoluteLeading()
        throws FB2toPDFException
    {
        return getLeadingDimension().getPoints(getFontSize().getPoints());
    }

    public float getRelativeLeading()
        throws FB2toPDFException
    {
        return 0.0f;
    }

    public int getAlignment()
        throws FB2toPDFException
    {
        if (alignment != null)
            return alignment.getAlignmentValue();

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getAlignment();

        // alignment default
        return Paragraph.ALIGN_LEFT;
    }

    private Dimension getSpacingBeforeDimension()
        throws FB2toPDFException
    {
        if (spacingBefore != null)
            return spacingBefore;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getSpacingBeforeDimension();

        // default value
        return new Dimension("0pt");
    }

    public float getSpacingBefore()
        throws FB2toPDFException
    {
        return getSpacingBeforeDimension().getPoints(getFontSize().getPoints());
    }

    private Dimension getSpacingAfterDimension()
        throws FB2toPDFException
    {
        if (spacingAfter != null)
            return spacingAfter;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getSpacingAfterDimension();

        // default value
        return new Dimension("0pt");
    }

    private Dimension getFirstSpacingBeforeDimension()
        throws FB2toPDFException
    {
        if (firstSpacingBefore != null)
            return firstSpacingBefore;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFirstSpacingBeforeDimension();

        // default value
        return getSpacingBeforeDimension();
    }

    public float getFirstSpacingBefore()
        throws FB2toPDFException
    {
        return getFirstSpacingBeforeDimension().getPoints(getFontSize().getPoints());
    }

    public float getSpacingAfter()
        throws FB2toPDFException
    {
        return getSpacingAfterDimension().getPoints(getFontSize().getPoints());
    }

    private Dimension getLastSpacingAfterDimension()
        throws FB2toPDFException
    {
        if (lastSpacingAfter != null)
            return lastSpacingAfter;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getLastSpacingAfterDimension();

        // default value
        return getSpacingAfterDimension();
    }

    public float getLastSpacingAfter()
        throws FB2toPDFException
    {
        return getLastSpacingAfterDimension().getPoints(getFontSize().getPoints());
    }

    private Dimension getLeftIndentDimension()
        throws FB2toPDFException
    {
        if (leftIndent != null)
            return leftIndent;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getLeftIndentDimension();

        // default value
        return new Dimension("0pt");
    }

    public float getLeftIndent()
        throws FB2toPDFException
    {
        return getLeftIndentDimension().getPoints(getFontSize().getPoints());
    }

    private Dimension getFirstLineIndentDimension()
        throws FB2toPDFException
    {
        if (firstLineIndent != null)
            return firstLineIndent;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFirstLineIndentDimension();

        // default value
        return new Dimension("0pt");
    }

    private Dimension getFirstFirstLineIndentDimension()
        throws FB2toPDFException
    {
        if (firstFirstLineIndent != null)
            return firstFirstLineIndent;

        ParagraphStyle baseStyle = getBaseStyle();
        if (baseStyle != null)
            return baseStyle.getFirstFirstLineIndentDimension();

        // default value
        return new Dimension("0pt");
    }

    public float getFirstLineIndent()
        throws FB2toPDFException
    {
        return getFirstLineIndentDimension().getPoints(getFontSize().getPoints());
    }

    public float getFirstFirstLineIndent()
        throws FB2toPDFException
    {
        return getFirstFirstLineIndentDimension().getPoints(getFontSize().getPoints());
    }

    public String getText()
    {
        return text;
    }


    public Chunk createChunk()
        throws FB2toPDFException
    {
        Chunk chunk = new Chunk();
        chunk.setFont(getFont());
        return chunk;
    }

    public Anchor createAnchor()
        throws FB2toPDFException
    {
        return new Anchor(getAbsoluteLeading());
    }

    public Anchor createInvisibleAnchor()
        throws FB2toPDFException
    {
        Chunk chunk = new Chunk();
        chunk.setFont(getTinyFont());
        chunk.append(".");
        Anchor anchor = new Anchor(0.01f);
        anchor.add(chunk);
        return anchor;
    }

    public Paragraph createParagraph()
        throws FB2toPDFException
    {
        Paragraph para = new Paragraph();
        para.setLeading(getAbsoluteLeading(), getRelativeLeading());
        para.setAlignment(getAlignment());
        para.setSpacingBefore(getSpacingBefore());
        para.setSpacingAfter(getSpacingAfter());
        para.setIndentationLeft(getLeftIndent());
        para.setFirstLineIndent(getFirstLineIndent());
        return para;
    }

    public Paragraph createParagraph(boolean bFirst, boolean bLast)
        throws FB2toPDFException
    {
        Paragraph para = createParagraph();

        if (bFirst) {
            para.setSpacingBefore(getFirstSpacingBefore());
            para.setFirstLineIndent(getFirstFirstLineIndent());
        }
        if (bLast)
            para.setSpacingAfter(getLastSpacingAfter());

        return para;
    }
}
