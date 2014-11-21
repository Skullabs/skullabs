package skullabs.processor;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.apache.activemq.ActiveMQConnectionFactory;

import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.core.endpoint.MQProvider;

import com.typesafe.config.Config;

@Getter
@Accessors( fluent = true )
@Singleton( exposedAs = MQProvider.class )
@NoArgsConstructor
public class ActiveMQMessageQueueProvider implements MQProvider {

	@Getter( lazy = true )
	private final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory( url() );

	@Getter( lazy = true )
	private final String url = config.getString( "skul.activemq" );

	@Provided
	Config config;

	@Override
	public QueueConnection createWorkerConnection() throws JMSException {
		final ActiveMQConnectionFactory factory = connectionFactory();
		return factory.createQueueConnection();
	}

	@Override
	public TopicConnection createSubscriptionConnection() throws JMSException {
		return connectionFactory().createTopicConnection();
	}
}