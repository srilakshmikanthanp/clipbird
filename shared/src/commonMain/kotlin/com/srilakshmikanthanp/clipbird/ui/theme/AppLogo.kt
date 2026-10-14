package com.srilakshmikanthanp.clipbird.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import clipbird.shared.generated.resources.Res
import clipbird.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun appLogoPainter(): Painter = painterResource(Res.drawable.logo)
