package mpicbg.image;

public class StreamRandomAccess
		extends StreamCursor
		implements RandomAccess, Localizable, LocalizableFactory< StreamRandomAccess >
{
	final protected int[] step;
	final protected int[] iByDim;
	protected int i = 0;
	
	StreamRandomAccess( Stream container, Access accessStrategy )
	{
		super( container, null, accessStrategy );
		int nd = container.getNumDim();
		iByDim = new int[nd];		
		step = new int[ nd ];
		step[ 0 ] = container.getPixelType().getNumChannels();
		for ( int d = 1; d < nd; ++d )
			step[ d ] = step[ d - 1 ] * container.getDim( d - 1 );		
	}

	StreamRandomAccess( Stream container )
	{
		this( container, new AccessDirect() );
	}


	StreamRandomAccess( Stream container, int[] l, Access accessStrategy )
	{
		this( container, accessStrategy );
		to( l );
	}

	StreamRandomAccess( Stream container, int[] l )
	{
		this( container, l, new AccessDirect() );
	}
	
	/**
	 * Constructor at the floor of a given initial location.
	 * 
	 * @param container
	 * @param l initial location
	 */
	StreamRandomAccess( Stream container, float[] l, Access accessStrategy )
	{
		this( container, accessStrategy );
		to( l );
	}

	StreamRandomAccess( Stream container, float[] l )
	{
		this( container, l, new AccessDirect() );
	}

	final public boolean isInside()
	{
		boolean a = true;
		for ( int i = 0; a && i < iByDim.length; ++i )
		{
			a &= iByDim[ i ] >= 0;
			a &= iByDim[ i ] < container.getDim( i );
		}
		return a;
	}

	final public void to( final int[] l )
	{
		i = l[ 0 ] * step[ 0 ];
		iByDim[ 0 ] = l[ 0 ];
		for ( int d = 1; d < step.length; ++d )
		{
			iByDim[ d ] = l[ d ];
			i += l[ d ] * step[ d ];
		}
	}
	
	final public void to( final float[] l )
	{
		iByDim[ 0 ] = l[ 0 ] > 0 ? ( int )l[ 0 ] : ( int )l[ 0 ] - 1;
		i = iByDim[ 0 ] * step[ 0 ];
		for ( int d = 1; d < l.length; ++d )
		{
			iByDim[ d ] = l[ d ] > 0 ? ( int )l[ d ] : ( int )l[ d ] - 1;
			i += iByDim[ d ] * step[ d ];
		}
	}
	
	final public float[] localize()
	{
		float[] l = new float[ iByDim.length ];
		localize( l );
		return l;
	}
	final public void localize( final float[] l )
	{
		for ( int d = 0; d < l.length; ++d )
			l[ d ] = iByDim[ d ];
	}
	final public void localize( final int[] l )
	{
		for ( int d = 0; d < l.length; ++d )
			l[ d ] = iByDim[ d ];
	}

	final public int getStreamIndex() { return i; }

	final public IteratorByDimension toIteratableByDimension( )
	{
		return new StreamIteratorByDimension( container, iByDim, accessStrategy );
	}

	final public RandomAccess toRandomAccessible( )
	{
		return new StreamRandomAccess( container, iByDim, accessStrategy );
	}
}