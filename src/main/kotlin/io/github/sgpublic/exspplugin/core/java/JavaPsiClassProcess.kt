package io.github.sgpublic.exspplugin.core.java

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exspplugin.base.PsiMethodBuilder
import io.github.sgpublic.exspplugin.base.PsiProcess
import io.github.sgpublic.exspplugin.util.SetterName
import io.github.sgpublic.exspplugin.util.createEditorClass
import io.github.sgpublic.exspplugin.util.getType
import io.github.sgpublic.exspplugin.util.log

open class JavaPsiClassProcess(clazz: PsiClass): PsiProcess<PsiClass, PsiClass>(clazz) {
    override fun process(): Collection<PsiClass> {
        val result = mutableListOf<PsiClass>()

        val annotation = OriginElement.getAnnotation(ExSharedPreference::class.java.canonicalName) ?: return result

        val Editor = OriginElement.createEditorClass()

        for (field in OriginElement.fields) {
            PsiMethodBuilder(Editor.manager, JavaLanguage.INSTANCE, field.SetterName)
                .addModifiers(PsiModifier.PUBLIC)
                .setMethodReturnType(Editor.getType())
                .addParameter("value", field.type)
                .setContainingClass(field.containingClass)
                .also {
                    it.navigationElement = field
                }
                .let {
                    Editor.addMethod(it)
                }
        }

        PsiMethodBuilder(Editor.manager, JavaLanguage.INSTANCE, "apply")
            .addModifiers(PsiModifier.PUBLIC)
            .setMethodReturnType(PsiType.VOID)
            .setContainingClass(OriginElement)
            .also {
                it.navigationElement = annotation
            }
            .let {
                Editor.addMethod(it)
            }

        result.add(Editor)

        return result
    }

    override val Name: String = OriginElement.name ?: ""
}