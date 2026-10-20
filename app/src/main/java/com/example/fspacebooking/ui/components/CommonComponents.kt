package com.example.fspacebooking.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.ui.theme.AppBorder
import com.example.fspacebooking.ui.theme.AppBorderSubtle
import com.example.fspacebooking.ui.theme.AppCardSurface
import com.example.fspacebooking.ui.theme.AppTextMuted
import com.example.fspacebooking.ui.theme.AppTextPrimary
import com.example.fspacebooking.ui.theme.AppTextSecondary
import com.example.fspacebooking.ui.theme.FptBlue
import com.example.fspacebooking.ui.theme.FptEmerald
import com.example.fspacebooking.ui.theme.FptEmeraldLight
import com.example.fspacebooking.ui.theme.FptRose
import com.example.fspacebooking.ui.theme.FptRoseLight

/**
 * Modern pill-style segmented control used across search and booking screens.
 */
@Composable
fun ModernSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppBorderSubtle)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEachIndexed { index, title ->
                val isSelected = selectedIndex == index
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) FptBlue else AppTextSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "segTextColor"
                )

                val itemModifier = if (isSelected) {
                    Modifier
                        .weight(1f)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                } else {
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                }

                Box(
                    modifier = itemModifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}

/**
 * Amenity chip with subtle border and optional icon.
 */
@Composable
fun AmenityChip(
    name: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    highlight: Boolean = false
) {
    val bgColor = if (highlight) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
    val borderColor = if (highlight) Color(0xFFBFDBFE) else AppBorder
    val textColor = if (highlight) FptBlue else Color(0xFF334155)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = textColor
            )
        )
    }
}

@Composable
fun FButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isSecondary: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1.0f, label = "buttonScale")

    val containerColor = if (isSecondary) {
        Color(0xFFEFF6FF)
    } else {
        FptBlue
    }
    val contentColor = if (isSecondary) {
        FptBlue
    } else {
        Color.White
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.35f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .height(52.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        if (enabled && !isLoading) onClick()
                    }
                )
            },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSecondary) 0.dp else 1.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.2.sp
                    )
                )
            }
        }
    }
}

@Composable
fun FTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)) },
        placeholder = placeholder?.let { { Text(it, color = AppTextMuted, fontSize = 13.sp) } },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else FptBlue.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FptBlue,
            unfocusedBorderColor = AppBorder,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = AppCardSurface,
            unfocusedContainerColor = AppCardSurface,
            focusedLabelColor = FptBlue,
            unfocusedLabelColor = AppTextSecondary,
            focusedTextColor = AppTextPrimary,
            unfocusedTextColor = AppTextPrimary
        ),
        modifier = modifier.fillMaxWidth()
    )
    if (isError && !errorMessage.isNullOrEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(
    status: ResourceStatus,
    modifier: Modifier = Modifier
) {
    val (dotColor, bgColor, label) = when (status) {
        ResourceStatus.AVAILABLE -> Triple(FptEmerald, FptEmeraldLight, "Trống")
        ResourceStatus.BOOKED -> Triple(FptRose, FptRoseLight, "Đã đặt")
        ResourceStatus.MAINTENANCE -> Triple(Color(0xFFF59E0B), Color(0xFFFEF3C7), "Bảo trì")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = dotColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (dotColor, bgColor, label) = when (status) {
        BookingStatus.CONFIRMED -> Triple(FptEmerald, FptEmeraldLight, "Đã xác nhận")
        BookingStatus.CANCELLED -> Triple(FptRose, FptRoseLight, "Đã hủy")
        BookingStatus.CHECKED_IN -> Triple(FptBlue, Color(0xFFEFF6FF), "Đã nhận chỗ")
        BookingStatus.NO_SHOW -> Triple(Color(0xFFDC2626), Color(0xFFFEE2E2), "Vắng mặt")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = dotColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun FCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppCardSurface
        ),
        border = BorderStroke(width = 1.dp, color = AppBorder),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = cardModifier
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors
    )

    Box(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(12.dp))
    )
}
