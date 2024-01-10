package coffee.khyonieheart.synergy.api.gatedevents;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.event.EventPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GatedEventHandler
{
	EventPriority priority() default EventPriority.NORMAL;
	GatedBranch branch() default GatedBranch.BETA;
}
