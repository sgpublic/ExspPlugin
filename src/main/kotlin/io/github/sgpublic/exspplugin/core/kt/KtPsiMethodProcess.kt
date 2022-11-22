package io.github.sgpublic.exspplugin.core.kt

import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exspplugin.base.PsiProcess
import io.github.sgpublic.exspplugin.util.hasAnnotation
import io.github.sgpublic.exspplugin.util.ktParent
import io.github.sgpublic.exspplugin.util.log
import org.jetbrains.kotlin.idea.core.getOrCreateCompanionObject
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class KtPsiMethodProcess(clazz: KtObjectDeclaration): PsiProcess<KtObjectDeclaration, KtFunction>(clazz) {
    override fun process(): Collection<KtFunction> {
        if (!OriginElement.ktParent.hasAnnotation(ExSharedPreference::class.java)) {
            return emptyList()
        }

        log.info("Process kotlin method: $Name")
        val factory = KtPsiFactory(Project, true)
        return mutableListOf(factory.createFunction("fun edit(): Editor"))
    }

    override val Name: String = clazz.name ?: ""
}