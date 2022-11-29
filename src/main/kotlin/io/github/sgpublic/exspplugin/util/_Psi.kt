package io.github.sgpublic.exspplugin.util

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightPsiClassBuilder
import io.github.sgpublic.exspplugin.base.JavaEditorClassBuilder
import io.github.sgpublic.exspplugin.base.PsiMethodBuilder
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.lombok.utils.capitalize
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

fun LightPsiClassBuilder.addModifier(vararg modifiers: String): LightPsiClassBuilder {
    val list = modifierList
    for (modifier in modifiers) {
        list.addModifier(modifier)
    }
    return this
}

fun PsiClass.getType(): PsiType {
    return JavaPsiFacade.getElementFactory(project).createType(this)
}

val PsiField.IsBoolean: Boolean get() = type.equalsToText("boolean") || type.equalsToText("java.lang.Boolean")

val PsiField.GetterName: String get() {
    val name = name.capitalize()
    return if (IsBoolean) {
        "is$name"
    } else {
        "get$name"
    }
}

val PsiField.SetterName: String get() {
    val name = name.capitalize()
    return "set$name"
}

val KtProperty.SetterName: String get() {
    val name = name?.capitalize() ?: ""
    return "set$name"
}

fun PsiClass.createEditorClass(): LightPsiClassBuilder {
    val constructor = PsiMethodBuilder(manager, JavaLanguage.INSTANCE, name ?: "")
        .setConstructor(true)
        .addParameter("editor", "android.content.SharedPreferences.Editor")
    return JavaEditorClassBuilder(this)
        .addConstructor(constructor)
        .addModifier(PsiModifier.PUBLIC, PsiModifier.STATIC)
        .setContainingClass(this)
}

fun PsiClass.getEditorClass(): PsiClass {
    return findInnerClassByName("Editor", false)!!
}

fun KtObjectDeclaration.createEditorClass(): KtClass {
    return KtPsiFactory(project, true)
        .createClass("class ${fqName?.asString()}.Editor (private val editor: android.content.SharedPreferences.Editor)")
}

val KtObjectDeclaration.ktParent: KtClass get() {
    return parent as KtClass
}

fun KtClass.hasAnnotation(annotation: Class<out Annotation>): Boolean {
    log.info("check annotation for $name: $annotation")
    return annotations.any {
        log.info("annotation name: ${it.getKotlinFqName()?.asString()}")
        it.getKotlinFqName()?.asString() == annotation.javaClass.canonicalName
    }
}