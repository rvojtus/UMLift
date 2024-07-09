<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output method="xml" indent="yes"/>

    <!-- Template to match the root element of the diagram file -->
    <xsl:template match="/">
        <ecore:EPackage xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore" name="ConvertedPackage">
            <xsl:apply-templates select="diagram/element"/>
        </ecore:EPackage>

    </xsl:template>

    <!-- Template to match each element -->
    <xsl:template match="element">
        <eClassifiers xsi:type="ecore:EClass" name="ClassNAME">
            <xsl:apply-templates select="panel_attributes"/>
        </eClassifiers>
    </xsl:template>

    <!-- Template to match Attribute -->
    <xsl:template match="panel_attributes">
        <eStructuralFeatures xsi:type="ecore:EAttribute" name="AttribName" eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString"/>
    </xsl:template>


</xsl:stylesheet>