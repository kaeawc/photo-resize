package co.hinge.photoresize.main

import co.hinge.photoresize.models.Photo
import co.hinge.photoresize.storage.Storage
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainPresenterTest {

    @Mock lateinit var photo: Photo
    @Mock lateinit var view: MainPresenter.MainView
    @Mock lateinit var presenter: MainPresenter
    @Mock lateinit var interactor: MainInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        whenever(presenter.interactor).thenReturn(interactor)
        whenever(presenter.setView(eq(view))).thenCallRealMethod()
        whenever(presenter.destroy()).thenCallRealMethod()
        whenever(presenter.onPhotoLoaded(eq(photo))).thenCallRealMethod()
    }

    @Test
    fun `setting the view triggers requesting a photo`() {
        presenter.setView(view)
        verify(interactor, times(1)).requestPhoto()
    }

    @Test
    fun `when photo is loaded presenter will ask view to render it`() {
        presenter.setView(view)
        presenter.onPhotoLoaded(photo)
        verify(view, times(1)).showPhoto(eq(photo))
    }
}
